package com.maxwellnie.velox.jpa.core.template.proxy.executor.impl;

import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper;
import com.maxwellnie.velox.jpa.framework.exception.ExecutorException;
import com.maxwellnie.velox.jpa.framework.proxy.executor.query.BaseQueryExecutor;
import com.maxwellnie.velox.jpa.framework.sql.SimpleSqlFragment;
import com.maxwellnie.velox.jpa.framework.sql.SqlBuilder;
import com.maxwellnie.velox.jpa.framework.utils.SqlUtils;
import com.maxwellnie.velox.jpa.framework.utils.StatementUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class QueryExecutor extends BaseQueryExecutor {
    private static final Logger logger = LoggerFactory.getLogger(QueryExecutor.class);

    public QueryExecutor() {
        super(logger, Collections.EMPTY_LIST);
    }

    @Override
    protected void checkArgs(Object[] args) throws ExecutorException {
        if (args == null || args.length != 1) {
            throw new ExecutorException("Method of args is empty.");
        }
    }

    @Override
    protected void doAfterOpenStatement(StatementWrapper statementWrapper, List<Object> params, Object[] args) throws SQLException {
        PreparedStatement preparedStatement = statementWrapper.getPrepareStatement();
        super.doAfterOpenStatement(statementWrapper, params, args);
        StatementUtils.setParam(params, preparedStatement);
    }

    @Override
    protected void doBuildSelectSql(SimpleSqlFragment sqlFragment, List<ColumnInfo> columns, Object[] args, TableInfo tableInfo) {
        SqlBuilder<?> sqlBuilder = (SqlBuilder<?>) args[0];
        super.doBuildSelectSql(sqlFragment, columns, args, tableInfo);
        sqlFragment.addSql(SqlUtils.buildSql(sqlBuilder, sqlFragment.getParams()));
    }
}
