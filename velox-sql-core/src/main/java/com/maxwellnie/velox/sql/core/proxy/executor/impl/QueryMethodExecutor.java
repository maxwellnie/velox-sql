package com.maxwellnie.velox.sql.core.proxy.executor.impl;

import com.maxwellnie.velox.sql.core.cache.key.CacheKey;
import com.maxwellnie.velox.sql.core.meta.MetaData;
import com.maxwellnie.velox.sql.core.natives.exception.ExecutorException;
import com.maxwellnie.velox.sql.core.natives.jdbc.mapping.ReturnTypeMapping;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlType;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.row.QueryRowSqlFactory;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.row.RowSql;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.row.RowSqlFactory;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfo;
import com.maxwellnie.velox.sql.core.proxy.executor.result.SqlResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

/**
 * @author Maxwell Nie
 */
public class QueryMethodExecutor extends BaseMethodExecutor {
    public QueryMethodExecutor() {
        super(LoggerFactory.getLogger(QueryMethodExecutor.class));
    }

    public QueryMethodExecutor(Logger logger) {
        super(logger);
    }

    @Override
    public MetaData prepared(TableInfo tableInfo, Object[] args) throws ExecutorException {
        MetaData metaData = MetaData.ofEmpty();
        metaData.addProperty("tableInfo", tableInfo);
        metaData.addProperty("sqlType", SqlType.QUERY);
        metaData.addProperty("sqlDecorator", args[0]);
        return metaData;
    }

    @Override
    public RowSql buildRowSql(MetaData metaData) throws ExecutorException {
        RowSqlFactory rowSqlFactory = new QueryRowSqlFactory();
        return rowSqlFactory.getRowSql(metaData);
    }

    @Override
    public SqlResult handleRunnerResult(Object result, TableInfo tableInfo, CacheKey cacheKey, ReturnTypeMapping returnTypeMapping) throws ExecutorException {
        Object entityObjects = Collections.EMPTY_LIST;
        if (result != null) {
            try {
                entityObjects = tableInfo.getResultSetParser().parseResultSet((ResultSet) result, returnTypeMapping);
            } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                     IllegalAccessException e) {
                throw new ExecutorException(e);
            }
        }
        return new SqlResult(SqlResult.CacheFlush.FLUSH, entityObjects, cacheKey);
    }
}
