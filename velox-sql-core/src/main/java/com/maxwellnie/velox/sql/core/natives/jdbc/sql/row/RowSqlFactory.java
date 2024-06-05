package com.maxwellnie.velox.sql.core.natives.jdbc.sql.row;

import com.maxwellnie.velox.sql.core.meta.MetaData;
import com.maxwellnie.velox.sql.core.natives.exception.ExecutorException;

/**
 * @author Maxwell Nie
 */
public interface RowSqlFactory {
    RowSql getRowSql(MetaData metaData) throws ExecutorException;
}
