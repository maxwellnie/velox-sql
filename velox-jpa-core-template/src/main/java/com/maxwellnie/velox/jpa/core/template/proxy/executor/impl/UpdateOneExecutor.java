package com.maxwellnie.velox.jpa.core.template.proxy.executor.impl;

import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.primary.PrimaryKeyStrategy;
import com.maxwellnie.velox.jpa.core.manager.KeyStrategyManager;
import com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper;
import com.maxwellnie.velox.jpa.framework.exception.ExecutorException;
import com.maxwellnie.velox.jpa.framework.proxy.executor.update.BaseUpdateExecutor;
import com.maxwellnie.velox.jpa.framework.sql.SimpleSqlFragment;
import com.maxwellnie.velox.jpa.framework.sql.SqlBuilder;
import com.maxwellnie.velox.jpa.framework.utils.ExecutorUtils;
import com.maxwellnie.velox.jpa.framework.utils.SqlUtils;
import com.maxwellnie.velox.jpa.framework.utils.StatementUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class UpdateOneExecutor extends BaseUpdateExecutor {
    private static final Logger logger= LoggerFactory.getLogger(UpdateOneExecutor.class);
    public UpdateOneExecutor() {
        super(logger, 0);
    }

    @Override
    protected void checkArgs(Object[] args) throws ExecutorException {
        if (args == null || args.length != 2){
            throw new ExecutorException("Method of args is empty.");
        }
    }

    @Override
    protected PreparedStatement doOpenStatement(Connection connection, TableInfo tableInfo, String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    @Override
    protected void doAfterOpenStatement(StatementWrapper statementWrapper, List<Object> params, Object[] args) throws SQLException {
        PreparedStatement preparedStatement=statementWrapper.getPrepareStatement();
        Object[] entityInstances = new Object[]{args[0]};
        statementWrapper.getMetaData().addProperty("entityInstances",entityInstances);
        statementWrapper.setMode(StatementWrapper.UPDATE);
        StatementUtils.setParam(params, preparedStatement);
    }

    @Override
    protected void doBuildUpdateSql(SimpleSqlFragment updateSql, List<ColumnInfo> columns, Object[] args, TableInfo tableInfo) {
        SqlBuilder<?> sqlBuilder = (SqlBuilder<?>) args[1];
        StringBuffer sqlStr=new StringBuffer("UPDATE ").append(tableInfo.getTableName()).append(" SET ");
        for (ColumnInfo columnInfo:columns){
            sqlStr.append(columnInfo.getColumnName()).append("=?,");
            try {
                updateSql.addParam(columnInfo.getColumnMappedField().get(args[0]));
            } catch (IllegalAccessException e) {
                throw new ExecutorException(e);
            }
        }
        sqlStr.deleteCharAt(sqlStr.length()-1).append(SqlUtils.buildSql(sqlBuilder, updateSql.getParams()));
        updateSql.setNativeSql(sqlStr.toString());
    }
}
