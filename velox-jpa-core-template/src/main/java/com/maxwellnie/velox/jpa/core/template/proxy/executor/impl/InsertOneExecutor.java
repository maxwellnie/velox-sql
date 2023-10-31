package com.maxwellnie.velox.jpa.core.template.proxy.executor.impl;

import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper;
import com.maxwellnie.velox.jpa.framework.exception.ExecutorException;
import com.maxwellnie.velox.jpa.framework.proxy.executor.insert.BaseInsertExecutor;
import com.maxwellnie.velox.jpa.framework.sql.SimpleSqlFragment;
import com.maxwellnie.velox.jpa.framework.utils.StatementUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class InsertOneExecutor extends BaseInsertExecutor {
    private static final Logger logger = LoggerFactory.getLogger(InsertOneExecutor.class);

    public InsertOneExecutor() {
        super(logger, 0);
    }

    @Override
    protected void checkArgs(Object[] args) throws ExecutorException {
        if (args == null || args.length != 1) {
            throw new ExecutorException("Method of args is empty.");
        } else {
            if (args[0] == null) {
                throw new ExecutorException("Entity instance must be not null.");
            }
        }
    }

    @Override
    protected void doAfterOpenStatement(StatementWrapper statementWrapper, List<Object> params, Object[] args) throws SQLException {
        PreparedStatement preparedStatement = statementWrapper.getPrepareStatement();
        Object[] entityInstances = new Object[]{args[0]};
        statementWrapper.getMetaData().addProperty("entityInstances", entityInstances);
        super.doAfterOpenStatement(statementWrapper, params, args);
        StatementUtils.setParam(params, preparedStatement);
    }

    @Override
    protected void doBuildInsertSql(SimpleSqlFragment insertSql, List<ColumnInfo> columns, Object[] args, TableInfo tableInfo) throws ExecutorException {
        super.doBuildInsertSql(insertSql, columns, args, tableInfo);
        Object entityInstance = args[0];
        for (ColumnInfo columnInfo : columns) {
            try {
                insertSql.addParam(columnInfo.getColumnMappedField().get(entityInstance));
            } catch (IllegalAccessException e) {
                throw new ExecutorException(e);
            }
        }
    }
}
