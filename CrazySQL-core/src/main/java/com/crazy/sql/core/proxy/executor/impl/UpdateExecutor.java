package com.crazy.sql.core.proxy.executor.impl;

import com.crazy.sql.core.accessor.SqlBuilder;
import com.crazy.sql.core.config.GlobalConfig;
import com.crazy.sql.core.exception.EntityObjectException;
import com.crazy.sql.core.proxy.executor.Executor;
import com.crazy.sql.core.jdbc.context.JdbcContext;
import com.crazy.sql.core.jdbc.sql.UpdateStatement;
import com.crazy.sql.core.jdbc.table.column.ColumnInfo;
import com.crazy.sql.core.jdbc.table.TableInfo;
import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.jdbc.transaction.Transaction;
import com.crazy.sql.core.utils.jdbc.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Accessor.update()方法的执行器
 * @author Akiba no ichiichiyoha
 */
public class UpdateExecutor implements Executor {
    @Override
    public Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object,Object> cache, String accessorHashCode, Object[] args) {
        /**
         * 实体对象
         */
        Object o= args[0];
        if(o==null)
            throw new EntityObjectException("The entity object "+tableInfo.getMappedClazz() +" is null.");
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
        SqlBuilder a= args[1]==null?null:(SqlBuilder) args[1];
        try {
            /**
             * 开始执行sql语句
             */
            return openStatement(transaction.getConnection(), tableInfo,a,o);
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }
    private Object openStatement(Connection connection, TableInfo tableInfo, SqlBuilder sqlBuilder, Object o) throws IllegalAccessException, SQLException {
        UpdateStatement updateStatement=getUpdateStatement(tableInfo,o,sqlBuilder);
        updateStatement.integratingResource();
        String sql= updateStatement.getNativeSql();
        PreparedStatement preparedStatement=connection.prepareStatement(sql);
        JdbcUtils.setParam(updateStatement,preparedStatement);
        int row=preparedStatement.executeUpdate();
        preparedStatement.close();
        return row;
    }
    private UpdateStatement getUpdateStatement(TableInfo tableInfo,Object o,SqlBuilder sqlBuilder) throws IllegalAccessException {
        UpdateStatement updateStatement=new UpdateStatement();
        updateStatement.setTableName(tableInfo.getTableName());
        for (ColumnInfo columnInfo:tableInfo.getColumnMappedMap().values()){
            updateStatement.getColumns().add(columnInfo.getColumnName());
            updateStatement.getValues().add(columnInfo.getColumnMappedField().get(o));
        }
        if(sqlBuilder!=null){
            updateStatement.setWhereFragment(sqlBuilder.getWhereFragment());
            updateStatement.setLastFragment(sqlBuilder.getLastFragment());
        }
        return updateStatement;
    }
}
