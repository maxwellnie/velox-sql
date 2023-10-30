package com.maxwellnie.velox.jpa.core.jdbc.table.primary.keyselector;

import com.maxwellnie.velox.jpa.core.utils.jdbc.ResultSetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Statement;

public class JdbcSelector implements KeySelector {
    private static final Logger logger = LoggerFactory.getLogger(JdbcSelector.class);

    @Override
    public Object selectGeneratorKey(Object... params) {
        if (params == null || params.length == 0)
            return null;
        Statement statement = (Statement) params[0];
        try {
            return ResultSetUtils.getAutoIncrementKey(statement.getGeneratedKeys());
        } catch (SQLException e) {
            logger.error(e.getMessage() + "\t\n" + e.getCause());
            return null;
        }
    }
}
