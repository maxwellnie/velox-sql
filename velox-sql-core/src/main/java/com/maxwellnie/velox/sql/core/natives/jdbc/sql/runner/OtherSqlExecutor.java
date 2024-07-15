package com.maxwellnie.velox.sql.core.natives.jdbc.sql.runner;

import com.maxwellnie.velox.sql.core.natives.exception.ClassTypeException;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.row.RowSql;
import com.maxwellnie.velox.sql.core.natives.jdbc.statement.StatementWrapper;
import com.maxwellnie.velox.sql.core.utils.base.CollectionUtils;
import com.maxwellnie.velox.sql.core.utils.jdbc.StatementUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public class OtherSqlExecutor implements SqlExecutor<Object> {
    public static final OtherSqlExecutor INSTANCE = new OtherSqlExecutor();

    @Override
    public Object run(RowSql rowSql, StatementWrapper statementWrapper) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = statementWrapper.getPrepareStatement();
        } catch (ClassTypeException e) {
            throw new SQLException(e);
        }
        StatementUtils.setParam(CollectionUtils.first(rowSql.getParams()), preparedStatement, rowSql.getTypeConvertors());
        boolean methodType = preparedStatement.execute();
        if (methodType)
            return preparedStatement.getResultSet();
        return preparedStatement.getUpdateCount();
    }
}
