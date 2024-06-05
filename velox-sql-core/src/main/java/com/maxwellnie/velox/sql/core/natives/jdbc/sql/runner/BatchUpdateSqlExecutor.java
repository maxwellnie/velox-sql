package com.maxwellnie.velox.sql.core.natives.jdbc.sql.runner;

import com.maxwellnie.velox.sql.core.natives.exception.ClassTypeException;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.row.RowSql;
import com.maxwellnie.velox.sql.core.natives.jdbc.statement.StatementWrapper;
import com.maxwellnie.velox.sql.core.utils.jdbc.StatementUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public class BatchUpdateSqlExecutor implements SqlExecutor<int[]> {
    BatchUpdateSqlExecutor() {
    }

    @Override
    public int[] run(RowSql rowSql, StatementWrapper statementWrapper) throws SQLException, ClassTypeException {
        PreparedStatement preparedStatement = statementWrapper.getPrepareStatement();
        for (int i = 0; i < rowSql.getParams().size(); i++) {
            StatementUtils.setParam(rowSql.getParams().get(i), preparedStatement, rowSql.getTypeConvertors());
            preparedStatement.addBatch();
        }
        return preparedStatement.executeBatch();
    }
}
