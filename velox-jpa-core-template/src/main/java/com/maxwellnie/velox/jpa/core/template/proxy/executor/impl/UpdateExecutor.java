package com.maxwellnie.velox.jpa.core.template.proxy.executor.impl;

import com.maxwellnie.velox.jpa.core.dao.support.SqlBuilder;
import com.maxwellnie.velox.jpa.core.exception.EntityObjectException;
import com.maxwellnie.velox.jpa.core.jdbc.sql.UpdateStatement;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.jpa.core.jdbc.transaction.Transaction;
import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.velox.jpa.core.proxy.executor.BaseUpdateExecutor;
import com.maxwellnie.velox.jpa.core.utils.jdbc.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * DaoImpl.update()方法的执行器
 *
 * @author Maxwell Nie
 */
public class UpdateExecutor extends BaseUpdateExecutor {
    @Override
    public Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object, Object> cache, String daoImplHashCode, Object[] args) {
        /**
         * 实体对象
         */
        Object o = args[0];
        if (o == null)
            throw new EntityObjectException("The entity object " + tableInfo.getMappedClazz() + " is null.");
        /**
         * 获取事务
         */
        Transaction transaction = context.getTransaction();
        /**
         * SqlBuilder
         */
        SqlBuilder a = args[1] == null ? null : (SqlBuilder) args[1];
        try {
            /**
             * 开始执行sql语句
             */
            Object result = openStatement(transaction.getConnection(), tableInfo, a, o);
            flushCache(result, null, cache, context.getDirtyManager(), !context.getAutoCommit());
            return result;
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private Object openStatement(Connection connection, TableInfo tableInfo, SqlBuilder sqlBuilder, Object o) throws IllegalAccessException, SQLException {
        UpdateStatement updateStatement = getUpdateStatement(tableInfo, o, sqlBuilder);
        updateStatement.integratingResource();
        String sql = updateStatement.getNativeSql();
        int row = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            JdbcUtils.setParam(updateStatement, preparedStatement);
            row = preparedStatement.executeUpdate();
        }
        return row;
    }

    private UpdateStatement getUpdateStatement(TableInfo tableInfo, Object o, SqlBuilder sqlBuilder) throws IllegalAccessException {
        UpdateStatement updateStatement = new UpdateStatement();
        updateStatement.setTableName(tableInfo.getTableName());
        for (ColumnInfo columnInfo : tableInfo.getColumnMappedMap().values()) {
            updateStatement.getColumns().add(columnInfo.getColumnName());
            updateStatement.getValues().add(columnInfo.getColumnMappedField().get(o));
        }
        if (sqlBuilder != null) {
            updateStatement.setWhereFragment(sqlBuilder.getWhereFragment());
            updateStatement.setLastFragment(sqlBuilder.getLastFragment());
        }
        return updateStatement;
    }

}
