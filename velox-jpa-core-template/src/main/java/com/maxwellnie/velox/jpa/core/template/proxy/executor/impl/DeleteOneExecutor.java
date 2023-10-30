package com.maxwellnie.velox.jpa.core.template.proxy.executor.impl;

import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper;
import com.maxwellnie.velox.jpa.framework.exception.ExecutorException;
import com.maxwellnie.velox.jpa.framework.proxy.executor.delete.BaseDeleteExecutor;
import com.maxwellnie.velox.jpa.framework.sql.SimpleSqlFragment;
import com.maxwellnie.velox.jpa.framework.sql.SqlBuilder;
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
public class DeleteOneExecutor extends BaseDeleteExecutor {
    private static final Logger logger= LoggerFactory.getLogger(DeleteOneExecutor.class);
    public DeleteOneExecutor() {
        super(logger, 0);
    }
    @Override
    protected void checkArgs(Object[] args) throws ExecutorException {
        if (args == null || args.length != 1){
            throw new ExecutorException("Method of args is empty.");
        }
    }

    @Override
    protected PreparedStatement doOpenStatement(Connection connection,TableInfo tableInfo, String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    @Override
    protected void doAfterOpenStatement(StatementWrapper statementWrapper, List<Object> params, Object[] args) throws SQLException {
        PreparedStatement preparedStatement=statementWrapper.getPrepareStatement();
        statementWrapper.setMode(StatementWrapper.UPDATE);
        StatementUtils.setParam(params, preparedStatement);
    }

    @Override
    protected void doBuildDeleteSql(SimpleSqlFragment deleteSql, List<ColumnInfo> columns, Object[] args, TableInfo tableInfo) {
        SqlBuilder<?> sqlBuilder = (SqlBuilder<?>) args[0];
        StringBuffer sqlStr=new StringBuffer("DELETE ")
                .append(" FROM ").append(tableInfo.getTableName());
        sqlStr.append(SqlUtils.buildSql(sqlBuilder, deleteSql.getParams()));
        deleteSql.setNativeSql(sqlStr.toString());
    }
}
