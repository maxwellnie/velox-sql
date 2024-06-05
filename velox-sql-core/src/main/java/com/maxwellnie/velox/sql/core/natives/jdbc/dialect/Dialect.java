package com.maxwellnie.velox.sql.core.natives.jdbc.dialect;

import com.maxwellnie.velox.sql.core.natives.jdbc.sql.row.RowSql;

/**
 * @author Maxwell Nie
 */
public interface Dialect {
    RowSql getDialectRowSql(RowSql rowSql, long start, long offset);
}
