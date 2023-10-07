package com.maxwellnie.vleox.jpa.core.proxy.executor.impl;

import com.maxwellnie.vleox.jpa.core.cahce.Cache;
import com.maxwellnie.vleox.jpa.core.dao.support.SqlBuilder;
import com.maxwellnie.vleox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.vleox.jpa.core.jdbc.sql.DeleteStatement;
import com.maxwellnie.vleox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.vleox.jpa.core.jdbc.transaction.Transaction;
import com.maxwellnie.vleox.jpa.core.utils.jdbc.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * DaoImpl.delete()方法执行器
 *
 * @author Maxwell Nie
 */
public class DeleteExecutor extends BaseUpdateExecutor {
    @Override
    public Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object, Object> cache, String daoImplHashCode, Object[] args) {
        /**
         * 获取事务
         */
        Transaction transaction = context.getTransaction();
        /**
         * SqlBuilder
         */
        SqlBuilder o = args[0] == null ? null : (SqlBuilder) args[0];
        try {
            /**
             * 开始执行sql语句
             */
            Object result = openStatement(transaction.getConnection(), tableInfo, o);
            flushCache(result, null, cache, context.getDirtyManager(), !context.getAutoCommit());
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int openStatement(Connection connection, TableInfo tableInfo, SqlBuilder sqlBuilder) throws SQLException {
        /**
         * 获取DeleteStatement这个类是一个delete语句的抽象类。
         */
        DeleteStatement deleteStatement = getDeleteStatement(tableInfo, sqlBuilder);
        /**
         * 整合资源
         */
        deleteStatement.integratingResource();
        /**
         * 获取sql
         */
        String sql = deleteStatement.getNativeSql();
        int row = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            /**
             * 设置参数
             */
            JdbcUtils.setParam(deleteStatement, preparedStatement);
            row = preparedStatement.executeUpdate();
        }
        return row;
    }

    /**
     * 获取DeleteStatement对象
     *
     * @param tableInfo
     * @param sqlBuilder
     * @return
     */
    private DeleteStatement getDeleteStatement(TableInfo tableInfo, SqlBuilder sqlBuilder) {
        /**
         * 新建DeleteStatement对象
         */
        DeleteStatement deleteStatement = new DeleteStatement();
        /**
         * 设置表名
         */
        deleteStatement.setTableName(tableInfo.getTableName());
        /**
         * 判断SqlBuilder是为为空，不为空则注入WhereStatement和LastStatement片段到DeleteStatement
         */
        if (sqlBuilder != null) {
            deleteStatement.setWhereFragment(sqlBuilder.getWhereFragment());
            deleteStatement.setLastFragment(sqlBuilder.getLastFragment());
        }
        return deleteStatement;
    }
}
