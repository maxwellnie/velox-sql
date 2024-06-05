package com.maxwellnie.velox.sql.core.natives.jdbc.dialect;

import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlPool;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.row.RowSql;

/**
 * @author Maxwell Nie
 */
public class MySqlDialect implements Dialect {
    @Override
    public RowSql getDialectRowSql(RowSql rowSql, long start, long offset) {
        String sql = rowSql.getNativeSql();
        sql = sql + SqlPool.SPACE + "LIMIT " + start + ", " + offset;
        rowSql.setNativeSql(sql);
        return rowSql;
    }
}
