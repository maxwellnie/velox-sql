package com.maxwellnie.velox.sql.core.natives.jdbc.sql.runner;

import com.maxwellnie.velox.sql.core.natives.exception.ClassTypeException;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.row.RowSql;
import com.maxwellnie.velox.sql.core.natives.jdbc.statement.StatementWrapper;
import com.maxwellnie.velox.sql.core.utils.common.CollectionUtils;
import com.maxwellnie.velox.sql.core.utils.jdbc.StatementUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public class UpdateSqlExecutor implements SqlExecutor<Integer> {
    UpdateSqlExecutor() {
    }

    @Override
    public Integer run(RowSql rowSql, StatementWrapper statementWrapper) throws SQLException, ClassTypeException {
        PreparedStatement preparedStatement = statementWrapper.getPrepareStatement();
        StatementUtils.setParam(CollectionUtils.first(rowSql.getParams()), preparedStatement, rowSql.getTypeConvertors());
        return preparedStatement.executeUpdate();
    }
}
