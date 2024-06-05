package com.maxwellnie.velox.sql.core.natives.jdbc.sql.runner;

import com.maxwellnie.velox.sql.core.natives.exception.ClassTypeException;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlType;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.row.RowSql;
import com.maxwellnie.velox.sql.core.natives.jdbc.statement.StatementWrapper;

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SQL执行器
 *
 * @author Maxwell Nie
 */
public interface SqlExecutor<T> {
    /**
     * 享元模式
     */
    Map<SqlType, SqlExecutor<?>> SQL_RUNNER_MAP = Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * 获取SqlRunner
     *
     * @param sqlType
     * @param <T>
     * @return
     */
    static <T> SqlExecutor<T> get(SqlType sqlType) {
        if (SQL_RUNNER_MAP.containsKey(sqlType))
            return (SqlExecutor<T>) SQL_RUNNER_MAP.get(sqlType);
        else {
            // 双重检查，避免内存浪费
            synchronized (SQL_RUNNER_MAP) {
                SqlExecutor<?> sqlExecutor;
                if (sqlType.equals(SqlType.QUERY))
                    sqlExecutor = new QuerySqlExecutor();
                else if (sqlType.equals(SqlType.UPDATE))
                    sqlExecutor = new UpdateSqlExecutor();
                else if (sqlType.equals(SqlType.BATCH_UPDATE))
                    sqlExecutor = new BatchUpdateSqlExecutor();
                else
                    sqlExecutor = OtherSqlExecutor.INSTANCE;
                if (SQL_RUNNER_MAP.containsKey(sqlType))
                    return (SqlExecutor<T>) SQL_RUNNER_MAP.get(sqlType);
                else {
                    SQL_RUNNER_MAP.put(sqlType, sqlExecutor);
                    return (SqlExecutor<T>) sqlExecutor;
                }
            }
        }
    }

    T run(RowSql rowSql, StatementWrapper statementWrapper) throws SQLException, ClassTypeException;
}
