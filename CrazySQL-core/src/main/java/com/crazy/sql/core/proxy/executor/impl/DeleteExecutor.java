package com.crazy.sql.core.proxy.executor.impl;

import com.crazy.sql.core.accessor.SqlBuilder;
import com.crazy.sql.core.config.GlobalConfig;
import com.crazy.sql.core.proxy.executor.Executor;
import com.crazy.sql.core.jdbc.sql.DeleteStatement;
import com.crazy.sql.core.jdbc.table.TableInfo;
import com.crazy.sql.core.jdbc.context.JdbcContext;
import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.jdbc.transaction.Transaction;
import com.crazy.sql.core.utils.jdbc.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Accessor.delete()方法执行器
 * @author Akiba no ichiichiyoha
 */
public class DeleteExecutor implements Executor {
    @Override
    public Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object,Object> cache, String accessorHashCode, Object[] args) {
        /**
         * 获取事务
         */
        Transaction transaction=context.getTransaction();
        /**
         * 如果开启缓存，清空缓存
         */
        if(GlobalConfig.getInstance().isCache())
            context.getDirtyManager().get(cache).clear();
        /**
         * SqlBuilder
         */
        SqlBuilder o= args[0]==null?null: (SqlBuilder) args[0];
        try {
            /**
             * 开始执行sql语句
             */
            return openStatement(transaction.getConnection(), tableInfo,o);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    private int openStatement(Connection connection, TableInfo tableInfo, SqlBuilder sqlBuilder) throws SQLException {
        /**
         * 获取DeleteStatement这个类是一个delete语句的抽象类。
         */
        DeleteStatement deleteStatement=getDeleteStatement(tableInfo, sqlBuilder);
        /**
         * 整合资源
         */
        deleteStatement.integratingResource();
        /**
         * 获取sql
         */
        String sql=deleteStatement.getNativeSql();
        PreparedStatement preparedStatement=null;
        int row=0;
        try{
            /**
             * 预处理
             */
            preparedStatement=connection.prepareStatement(sql);
            /**
             * 设置参数
             */
            JdbcUtils.setParam(deleteStatement,preparedStatement);
            row=preparedStatement.executeUpdate();
        } catch (Exception e){
            throw e;
        }finally {
            if(preparedStatement!=null)
                preparedStatement.close();
        }
        return row;
    }

    /**
     * 获取DeleteStatement对象
     * @param tableInfo
     * @param sqlBuilder
     * @return
     */
    private DeleteStatement getDeleteStatement(TableInfo tableInfo, SqlBuilder sqlBuilder) {
        /**
         * 新建DeleteStatement对象
         */
        DeleteStatement deleteStatement=new DeleteStatement();
        /**
         * 设置表名
         */
        deleteStatement.setTableName(tableInfo.getTableName());
        /**
         * 判断SqlBuilder是为为空，不为空则注入WhereStatement和LastStatement片段到DeleteStatement
         */
        if(sqlBuilder !=null){
            deleteStatement.setWhereFragment(sqlBuilder.getWhereFragment());
            deleteStatement.setLastFragment(sqlBuilder.getLastFragment());
        }
        return deleteStatement;
    }
}
