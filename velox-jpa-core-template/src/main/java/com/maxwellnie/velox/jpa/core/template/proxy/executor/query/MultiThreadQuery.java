package com.maxwellnie.velox.jpa.core.template.proxy.executor.query;

import com.maxwellnie.velox.jpa.core.config.BaseConfig;
import com.maxwellnie.velox.jpa.core.dao.support.SqlBuilder;
import com.maxwellnie.velox.jpa.core.exception.ThreadPoolException;
import com.maxwellnie.velox.jpa.core.jdbc.sql.LimitFragment;
import com.maxwellnie.velox.jpa.core.jdbc.sql.SelectStatement;
import com.maxwellnie.velox.jpa.core.jdbc.sql.SizeStatement;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.jpa.core.utils.java.ObjectUtils;
import com.maxwellnie.velox.jpa.core.utils.jdbc.JdbcUtils;
import com.maxwellnie.velox.jpa.core.utils.jdbc.ResultSetUtils;
import com.maxwellnie.velox.jpa.core.utils.reflect.TableIfoUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 多线程查询（查询总数量，一次性多线程并发分片查询），这个过程会消耗大量的服务器资源，请避免使用这个查询方式，不同于以往的查询方式，这个查询方式需要自己开启<br/>
 * 如果你的服务器内存在2048Mb，且你的用户总量超过1w，不建议你使用这个方法。
 *
 * @author Maxwell Nie
 */
public class MultiThreadQuery {
    private int THREAD_COUNT = 0; // 线程数
    private DataSource dataSource;
    private BaseConfig baseConfig;

    public MultiThreadQuery(int cpuCore, DataSource dataSource,BaseConfig baseConfig) {
        this.THREAD_COUNT = cpuCore * 2;
        this.dataSource = dataSource;
        this.baseConfig=baseConfig;
    }

    public <E> void execute(SqlBuilder<E> sqlBuilder, QueryHandle<E> queryHandle, Class<E> clazz) throws SQLException {
        TableInfo tableInfo = TableIfoUtils.getTableInfo(clazz,baseConfig);
        SizeStatement sizeStatement = new SizeStatement();
        sizeStatement.setTableName(tableInfo.getTableName());
        /**
         * 判断SqlBuilder是否为空，不为空则进行处理，将各种sql片段注入到SelectStatement中
         */
        long start = handleSql(sqlBuilder, sizeStatement, tableInfo);
        long size = selectSize(sizeStatement);
        QueryBody queryBody = new QueryBody(size, start, tableInfo);
        queryBody.execute(sqlBuilder, queryHandle);
    }

    private <E> long handleSql(SqlBuilder<E> sqlBuilder, SizeStatement sizeStatement, TableInfo tableInfo) {
        long start = 0;
        if (sqlBuilder != null) {
            sizeStatement.setWhereFragment(sqlBuilder.getWhereFragment());
            sizeStatement.setGroupByStatement(sqlBuilder.getGroupByFragment());
            sizeStatement.setHavingStatement(sqlBuilder.getHavingFragment());
            sizeStatement.setOrderByFragment(sqlBuilder.getOrderByFragment());
            sizeStatement.setLimitFragment(sqlBuilder.getLimitFragment());
            sizeStatement.setLastFragment(sqlBuilder.getLastFragment());
            if (sqlBuilder.getColumnList().size() == 0) {
                /**
                 * 判断是否含有主键，如果有，将主键列名放入待查询列中
                 */
                if (tableInfo.hasPk()) {
                    sizeStatement.setPrimaryName(tableInfo.getPkColumn().getColumnName());
                    sizeStatement.getSelectedColumns().add(tableInfo.getPkColumn().getColumnName());
                }
                /**
                 * 遍历列信息，将列名放入待查询列中
                 */
                for (ColumnInfo columnInfo : tableInfo.getColumnMappedMap().values()) {
                    sizeStatement.getSelectedColumns().add(columnInfo.getColumnName());
                }
            } else {
                sizeStatement.getSelectedColumns().addAll(sqlBuilder.getColumnList());
            }
            if (ObjectUtils.notEmptyFragment(sqlBuilder.getLimitFragment())) {
                start = sqlBuilder.getLimitFragment().getStart();
            }
        } else {
            /**
             * 判断是否含有主键，如果有，将主键列名放入待查询列中
             */
            if (tableInfo.hasPk()) {
                sizeStatement.setPrimaryName(tableInfo.getPkColumn().getColumnName());
                sizeStatement.getSelectedColumns().add(tableInfo.getPkColumn().getColumnName());
            }
            /**
             * 遍历列信息，将列名放入待查询列中
             */
            for (ColumnInfo columnInfo : tableInfo.getColumnMappedMap().values()) {
                sizeStatement.getSelectedColumns().add(columnInfo.getColumnName());
            }
        }
        return start;
    }

    private long selectSize(SizeStatement sizeStatement) throws SQLException {
        Connection connection = dataSource.getConnection();
        sizeStatement.integratingResource();
        String sql = sizeStatement.getNativeSql();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        JdbcUtils.setParam(sizeStatement, preparedStatement);
        ResultSet resultSet = preparedStatement.executeQuery();
        long size = 0;
        if (resultSet.next()) {
            size = resultSet.getLong(1);
        }
        return size;
    }

    class QueryBody {
        long START = 0;
        long COUNT_SIZE = 0;
        long LAST_BATCH_SIZE = 0;
        long BATCH_SIZE = 0;
        TableInfo tableInfo;

        public QueryBody(long COUNT_SIZE, long START, TableInfo tableInfo) {
            this.START = START;
            this.tableInfo = tableInfo;
            this.COUNT_SIZE = COUNT_SIZE;
            if (COUNT_SIZE == 0 || THREAD_COUNT == 0)
                throw new ThreadPoolException("Cpu not set 0 core And count is not 0.");
            this.BATCH_SIZE = COUNT_SIZE / THREAD_COUNT;
            this.LAST_BATCH_SIZE = COUNT_SIZE % THREAD_COUNT;
        }

        <E> List<E> performQuery(TableInfo tableInfo, SqlBuilder<E> sqlBuilder, Connection connection, long startIndex, long offset) throws SQLException {
            SelectStatement selectStatement = getSelectStatement(sqlBuilder, tableInfo);
            LimitFragment limitFragment = new LimitFragment();
            limitFragment.setStart(startIndex);
            limitFragment.setOffset(offset);
            selectStatement.setLimitFragment(limitFragment);
            selectStatement.integratingResource();
            String sql = selectStatement.getNativeSql();
            List<E> list;
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            JdbcUtils.setParam(selectStatement, preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();
            list = ResultSetUtils.convertEntity(rs, tableInfo);
            rs.close();
            preparedStatement.close();
            connection.close();
            return list;
        }

        public <E> void execute(SqlBuilder<E> sqlBuilder, QueryHandle<E> queryHandle) {
            this.execute(dataSource, tableInfo, sqlBuilder, queryHandle);
        }

        <E> void execute(DataSource dataSource, TableInfo tableInfo, SqlBuilder<E> sqlBuilder, QueryHandle<E> queryHandle) {
            ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
            // 创建任务列表
            List<Callable<List<E>>> tasks = new ArrayList<>();
            for (int i = 0; i < THREAD_COUNT; i++) {
                final long startIndex = i * BATCH_SIZE + START;
                tasks.add(() -> performQuery(tableInfo, sqlBuilder, dataSource.getConnection(), startIndex, BATCH_SIZE));
            }
            if (LAST_BATCH_SIZE > 0) {
                final long startIndex = (BATCH_SIZE * THREAD_COUNT);
                final long offset = COUNT_SIZE - startIndex;
                tasks.add(() -> performQuery(tableInfo, sqlBuilder, dataSource.getConnection(), startIndex, offset));
            }
            try {
                // 执行并发查询
                List<Future<List<E>>> results = executorService.invokeAll(tasks);

                // 处理查询结果
                for (Future<List<E>> result : results) {
                    List<E> list = result.get(); // 获取查询结果
                    queryHandle.handle(list);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            // 关闭线程池
            executorService.shutdown();
        }

        /**
         * 获取SelectStatement对象
         *
         * @param sqlBuilder
         * @param tableInfo
         * @return
         */
        <E> SelectStatement getSelectStatement(SqlBuilder<E> sqlBuilder, TableInfo tableInfo) {
            /**
             * 创建对象
             */
            SelectStatement selectStatement = new SelectStatement();
            /**
             * 设置表名
             */
            selectStatement.setTableName(tableInfo.getTableName());
            /**
             * 判断是否含有主键，如果有，将主键列名放入待查询列中
             */
            if (tableInfo.hasPk()) {
                selectStatement.getSelectedColumns().add(tableInfo.getPkColumn().getColumnName());
            }
            /**
             * 遍历列信息，将列名放入待查询列中
             */
            for (ColumnInfo columnInfo : tableInfo.getColumnMappedMap().values()) {
                selectStatement.getSelectedColumns().add(columnInfo.getColumnName());
            }
            /**
             * 判断SqlBuilder是否为空，不为空则进行处理，将各种sql片段注入到SelectStatement中
             */
            if (sqlBuilder != null) {
                selectStatement.setWhereFragment(sqlBuilder.getWhereFragment());
                selectStatement.setGroupByStatement(sqlBuilder.getGroupByFragment());
                selectStatement.setHavingStatement(sqlBuilder.getHavingFragment());
                selectStatement.setOrderByFragment(sqlBuilder.getOrderByFragment());
                selectStatement.setLimitFragment(sqlBuilder.getLimitFragment());
                selectStatement.setLastFragment(sqlBuilder.getLastFragment());
            }
            return selectStatement;
        }
    }

}
