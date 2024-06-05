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
public class BatchSqlExecutor implements SqlExecutor<int[]> {
    @Override
    public int[] run(RowSql rowSql, StatementWrapper statementWrapper) throws SQLException, ClassTypeException {
        String[] allSql = rowSql.getNativeSql().split(";");
        assert allSql.length == rowSql.getParams().size() : "sql and params size not match";
        PreparedStatement preparedStatement = null;
        preparedStatement = statementWrapper.getPrepareStatement();
        for (int i = 0; i < rowSql.getParams().size(); i++) {
            preparedStatement.addBatch(allSql[i]);
            StatementUtils.setParam(rowSql.getParams().get(i), preparedStatement, rowSql.getTypeConvertors());
        }
        return preparedStatement.executeBatch();
    }
}
