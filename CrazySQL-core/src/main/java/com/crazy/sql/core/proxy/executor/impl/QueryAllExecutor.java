package com.crazy.sql.core.proxy.executor.impl;

import com.crazy.sql.core.accessor.SqlBuilder;
import com.crazy.sql.core.cahce.dirty.DirtyManager;
import com.crazy.sql.core.cahce.key.CacheKey;
import com.crazy.sql.core.config.GlobalConfig;
import com.crazy.sql.core.proxy.executor.Executor;
import com.crazy.sql.core.jdbc.sql.SelectStatement;
import com.crazy.sql.core.jdbc.table.column.ColumnInfo;
import com.crazy.sql.core.jdbc.table.TableInfo;
import com.crazy.sql.core.jdbc.context.JdbcContext;
import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.jdbc.transaction.Transaction;
import com.crazy.sql.core.utils.jdbc.JdbcUtils;
import com.crazy.sql.core.utils.jdbc.ResultSetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;


/**
 * Accessor.queryAll()方法执行器
 * @author Akiba no ichiichiyoha
 */
public class QueryAllExecutor implements Executor {
    private static final Logger logger= LoggerFactory.getLogger(QueryAllExecutor.class);
    @Override
    public Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object,Object> cache, String accessorHashCode, Object[] args) {
        /**
         * 检查sql构造器是否为空
         */
        SqlBuilder sqlBuilder =  args[0]==null?null:(SqlBuilder) args[0];
        /**
         * 获取事务
         */
        Transaction transaction=context.getTransaction();
        /**
         * 获取脏数据管理器
         */
        DirtyManager dirtyManager=context.getDirtyManager();
        try {
            /**
             * 执行select语句
             */
            Object result= openStatement(transaction.getConnection(),tableInfo, sqlBuilder,cache,accessorHashCode,dirtyManager);
            /**
             * 获取到的数据，一般是一个List对象
             */
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 执行SELECT类型sql语句
     * @param connection
     * @param tableInfo
     * @param sqlBuilder
     * @param cache
     * @param accessorHashCode
     * @param dirtyManager
     * @return
     * @throws SQLException
     */
    private Object openStatement(Connection connection, TableInfo tableInfo, SqlBuilder sqlBuilder, Cache<Object,Object> cache, String accessorHashCode, DirtyManager dirtyManager) throws SQLException {
        /**
         * 获取SelectStatement这是一个select语句的抽象类
         */
        SelectStatement selectStatement=getSelectStatement(sqlBuilder,tableInfo);
        /**
         * 整合资源
         */
        selectStatement.integratingResource();
        /**
         * 获取sql
         */
        String sql=selectStatement.getNativeSql();
        logger.debug("queryAll() - sql:"+sql);
        Object list=null;
        /**
         * 创建缓存的key
         */
        CacheKey key=new CacheKey(tableInfo.getMappedClazz(),sql,accessorHashCode);
        if(GlobalConfig.getInstance().isCache()){
            /**
             * 获取缓存中的数据
             */
            list=cache.get(key);
        }
        /**
         * 如果数据为空，那么从数据库中获取数据。
         */
        if(list==null){
            PreparedStatement preparedStatement=null;
            try{
                long start=System.currentTimeMillis();
                /**
                 * 预处理
                 */
                preparedStatement=connection.prepareStatement(sql);
                /**
                 * 设置参数
                 */
                JdbcUtils.setParam(selectStatement,preparedStatement);
                ResultSet rs=preparedStatement.executeQuery();
                /**
                 * 设置分片值，默认为0
                 */
                rs.setFetchSize(tableInfo.getFetchSize());
                /**
                 * 转换ResultSet数据到实体类中
                 */
                list=ResultSetUtils.convertEntity(rs,tableInfo);
                rs.close();
                if(GlobalConfig.getInstance().isCache())
                    /**
                     * 写入脏数据管理器
                     */
                    dirtyManager.get(cache).put(key,list);
                logger.debug("Select completed,Elapsed time:"+(System.currentTimeMillis()-start)+"ms");
            }catch (Exception e){
                throw e;
            }finally {
                long s1=System.currentTimeMillis();
                if(preparedStatement!=null)
                    preparedStatement.close();
                System.out.println(System.currentTimeMillis()-s1);
            }
        }else
            logger.debug("Cache hit for key:"+key);
        return list;
    }

    /**
     * 获取SelectStatement对象
     * @param sqlBuilder
     * @param tableInfo
     * @return
     */
    private SelectStatement getSelectStatement(SqlBuilder sqlBuilder, TableInfo tableInfo){
        /**
         * 创建对象
         */
        SelectStatement selectStatement=new SelectStatement();
        /**
         * 设置表名
         */
        selectStatement.setTableName(tableInfo.getTableName());
        /**
         * 判断SqlBuilder是否为空，不为空则进行处理，将各种sql片段注入到SelectStatement中
         */
        if(sqlBuilder !=null){
            selectStatement.setWhereFragment(sqlBuilder.getWhereFragment());
            selectStatement.setGroupByStatement(sqlBuilder.getGroupByFragment());
            selectStatement.setHavingStatement(sqlBuilder.getHavingFragment());
            selectStatement.setOrderByFragment(sqlBuilder.getOrderByFragment());
            selectStatement.setLimitFragment(sqlBuilder.getLimitFragment());
            selectStatement.setLastFragment(sqlBuilder.getLastFragment());
            if(sqlBuilder.getColumnList().size()==0){
                /**
                 * 判断是否含有主键，如果有，将主键列名放入待查询列中
                 */
                if(tableInfo.hasPk()) {
                    selectStatement.getSelectedColumns().add(tableInfo.getPkColumn().getColumnName());
                }
                /**
                 * 遍历列信息，将列名放入待查询列中
                 */
                for (ColumnInfo columnInfo:tableInfo.getColumnMappedMap().values()) {
                    selectStatement.getSelectedColumns().add(columnInfo.getColumnName());
                }
            }else {
                selectStatement.getSelectedColumns().addAll(sqlBuilder.getColumnList());
            }
        }else {
            /**
             * 判断是否含有主键，如果有，将主键列名放入待查询列中
             */
            if(tableInfo.hasPk()) {
                selectStatement.getSelectedColumns().add(tableInfo.getPkColumn().getColumnName());
            }
            /**
             * 遍历列信息，将列名放入待查询列中
             */
            for (ColumnInfo columnInfo:tableInfo.getColumnMappedMap().values()) {
                selectStatement.getSelectedColumns().add(columnInfo.getColumnName());
            }
        }

        return selectStatement;
    }
}
