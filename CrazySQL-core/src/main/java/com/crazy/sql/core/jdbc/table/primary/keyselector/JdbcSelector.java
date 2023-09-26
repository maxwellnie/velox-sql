package com.crazy.sql.core.jdbc.table.primary.keyselector;

import com.crazy.sql.core.utils.jdbc.ResultSetUtils;

import java.sql.SQLException;
import java.sql.Statement;

public class JdbcSelector implements KeySelector{
    @Override
    public Object selectGeneratorKey(Object param) {
        if (param==null)
            return null;
        Statement statement=(Statement) param;
        try {
            return ResultSetUtils.getAutoIncrementKey(statement.getGeneratedKeys());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
