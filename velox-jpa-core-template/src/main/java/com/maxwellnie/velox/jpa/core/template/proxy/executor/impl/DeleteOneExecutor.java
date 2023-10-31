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

import java.sql.SQLException;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class DeleteOneExecutor extends BaseDeleteExecutor {
    private static final Logger logger = LoggerFactory.getLogger(DeleteOneExecutor.class);

    public DeleteOneExecutor() {
        super(logger, 0);
    }

    @Override
    protected void checkArgs(Object[] args) throws ExecutorException {
        if (args == null || args.length != 1) {
            throw new ExecutorException("Method of args is empty.");
        }
    }

    @Override
    protected void doAfterOpenStatement(StatementWrapper statementWrapper, List<Object> params, Object[] args) throws SQLException {
        super.doAfterOpenStatement(statementWrapper, params, args);
        StatementUtils.setParam(params, statementWrapper.getPrepareStatement());
    }

    @Override
    protected void doBuildDeleteSql(SimpleSqlFragment deleteSql, List<ColumnInfo> columns, Object[] args, TableInfo tableInfo) {
        super.doBuildDeleteSql(deleteSql, columns, args, tableInfo);
        SqlBuilder<?> sqlBuilder = (SqlBuilder<?>) args[0];
        deleteSql.addSql(SqlUtils.buildSql(sqlBuilder, deleteSql.getParams()) + ";");
    }
}
