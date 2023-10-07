package com.maxwellnie.vleox.jpa.core.proxy.executor.impl;

import com.maxwellnie.vleox.jpa.core.cahce.Cache;
import com.maxwellnie.vleox.jpa.core.cahce.dirty.CacheDirtyManager;
import com.maxwellnie.vleox.jpa.core.cahce.key.CacheKey;
import com.maxwellnie.vleox.jpa.core.dao.support.SqlBuilder;
import com.maxwellnie.vleox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.vleox.jpa.core.jdbc.sql.SelectStatement;
import com.maxwellnie.vleox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.vleox.jpa.core.jdbc.table.column.ColumnInfo;
import com.maxwellnie.vleox.jpa.core.jdbc.transaction.Transaction;
import com.maxwellnie.vleox.jpa.core.utils.jdbc.JdbcUtils;
import com.maxwellnie.vleox.jpa.core.utils.jdbc.ResultSetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * DaoImpl.queryAll()方法执行器
 *
 * @author Maxwell Nie
 */
public class QueryAllExecutor extends BaseQueryExecutor {
    private static final Logger logger = LoggerFactory.getLogger(QueryAllExecutor.class);

    @Override
    public Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object, Object> cache, String daoImplHashCode, Object[] args) {
        /**
         * 检查sql构造器是否为空
         */
        SqlBuilder sqlBuilder = args[0] == null ? null : (SqlBuilder) args[0];
        /**
         * 获取事务
         */
        Transaction transaction = context.getTransaction();
        /**
         * 获取脏数据管理器
         */
        CacheDirtyManager cacheDirtyManager = context.getDirtyManager();
        try {
            /**
             * 执行select语句
             */
            Map<String, Object> resultMap = openStatement(transaction.getConnection(), tableInfo, sqlBuilder, cache, daoImplHashCode, cacheDirtyManager);
            /**
             * 获取到的数据，一般是一个List对象
             */
            Object result = resultMap.get("result");
            CacheKey cacheKey = (CacheKey) resultMap.get("cacheKey");
            flushCache(result, cacheKey, cache, cacheDirtyManager, !context.getAutoCommit());
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 执行SELECT类型sql语句
     *
     * @param connection
     * @param tableInfo
     * @param sqlBuilder
     * @param cache
     * @param daoImplHashCode
     * @param cacheDirtyManager
     * @return
     * @throws SQLException
     */
    @Override
    protected Map<String, Object> openStatement(Connection connection, TableInfo tableInfo, SqlBuilder sqlBuilder, Cache<Object, Object> cache, String daoImplHashCode, CacheDirtyManager cacheDirtyManager) throws SQLException {
        /**
         * 获取SelectStatement这是一个select语句的抽象类
         */
        SelectStatement selectStatement = getSelectStatement(sqlBuilder, tableInfo);
        /**
         * 整合资源
         */
        selectStatement.integratingResource();
        /**
         * 获取sql
         */
        String sql = selectStatement.getNativeSql();
        logger.debug("queryAll() - sql:" + sql);
        Map<String, Object> map = new LinkedHashMap<>();
        Object list = null;
        /**
         * 创建缓存的key
         */
        CacheKey key = new CacheKey(tableInfo.getMappedClazz(), sql, daoImplHashCode);
        key.addValueCollection(selectStatement.getValues());
        map.put("cacheKey", key);
        if (cache != null) {
            /**
             * 获取缓存中的数据
             */
            list = cache.get(key);
        }
        /**
         * 如果数据为空，那么从数据库中获取数据。
         */
        if (list == null) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                long start = System.currentTimeMillis();
                /**
                 * 设置参数
                 */
                JdbcUtils.setParam(selectStatement, preparedStatement);
                ResultSet rs = preparedStatement.executeQuery();
                /**
                 * 设置分片值，默认为0
                 */
                rs.setFetchSize(tableInfo.getFetchSize());
                /**
                 * 转换ResultSet数据到实体类中
                 */
                list = ResultSetUtils.convertEntity(rs, tableInfo);
                rs.close();
                logger.debug("Select completed,Elapsed time:" + (System.currentTimeMillis() - start) + "ms");
            }
        } else
            logger.debug("Cache hit for key:" + key);
        map.put("result", list);
        return map;
    }

    /**
     * 获取SelectStatement对象
     *
     * @param sqlBuilder
     * @param tableInfo
     * @return
     */
    private SelectStatement getSelectStatement(SqlBuilder sqlBuilder, TableInfo tableInfo) {
        /**
         * 创建对象
         */
        SelectStatement selectStatement = new SelectStatement();
        /**
         * 设置表名
         */
        selectStatement.setTableName(tableInfo.getTableName());
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
            if (sqlBuilder.getColumnList().size() == 0) {
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
            } else {
                selectStatement.getSelectedColumns().addAll(sqlBuilder.getColumnList());
            }
        } else {
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
        }

        return selectStatement;
    }
}
