package com.maxwellnie.velox.jpa.core.template.proxy.executor.impl;

import com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper;
import com.maxwellnie.velox.jpa.framework.exception.ExecutorException;
import com.maxwellnie.velox.jpa.framework.proxy.executor.update.BaseUpdateExecutor;
import com.maxwellnie.velox.jpa.framework.utils.StatementUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class UpdateOneExecutor extends BaseUpdateExecutor {
    private static final Logger logger = LoggerFactory.getLogger(UpdateOneExecutor.class);

    public UpdateOneExecutor() {
        super(logger, 0);
    }

    @Override
    protected void checkArgs(Object[] args) throws ExecutorException {
        if (args == null || args.length != 2) {
            throw new ExecutorException("Method of args is empty.");
        }
    }

    @Override
    protected void doAfterOpenStatement(StatementWrapper statementWrapper, List<Object> params, Object[] args) throws SQLException {
        PreparedStatement preparedStatement = statementWrapper.getPrepareStatement();
        Object[] entityInstances = new Object[]{args[0]};
        statementWrapper.getMetaData().addProperty("entityInstances", entityInstances);
        StatementUtils.setParam(params, preparedStatement);
    }
}
