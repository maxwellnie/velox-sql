package com.maxwellnie.velox.sql.core.proxy.executor.impl;

import com.maxwellnie.velox.sql.core.cache.key.CacheKey;
import com.maxwellnie.velox.sql.core.distributed.TransactionTask;
import com.maxwellnie.velox.sql.core.meta.MetaData;
import com.maxwellnie.velox.sql.core.natives.exception.ClassTypeException;
import com.maxwellnie.velox.sql.core.natives.exception.ExecutorException;
import com.maxwellnie.velox.sql.core.natives.jdbc.mapping.ReturnTypeMapping;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSession;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlType;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.row.InsertRowSqlFactory;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.row.RowSql;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.row.RowSqlFactory;
import com.maxwellnie.velox.sql.core.natives.jdbc.statement.StatementWrapper;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfo;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.primary.KeyStrategyManager;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.primary.generator.KeyGenerator;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.primary.generator.NoKeyGenerator;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.primary.keyselector.JdbcSelector;
import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.impl.jdbc.Connections;
import com.maxwellnie.velox.sql.core.proxy.executor.result.SqlResult;
import com.maxwellnie.velox.sql.core.utils.base.MetaWrapperUtils;
import com.maxwellnie.velox.sql.core.utils.log.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Maxwell Nie
 */
public class InsertMethodExecutor extends BaseMethodExecutor {
    public InsertMethodExecutor() {
        super(LoggerFactory.getLogger(InsertMethodExecutor.class));
    }

    public InsertMethodExecutor(Logger logger) {
        super(logger);
    }

    @Override
    public MetaData prepared(TableInfo tableInfo, Object[] args) throws ExecutorException {
        MetaData metaData = MetaData.ofEmpty();
        metaData.addProperty("tableInfo", tableInfo);
        if (Collection.class.isAssignableFrom(args[0].getClass())) {
            metaData.addProperty("sqlType", SqlType.BATCH_UPDATE);
            metaData.addProperty("entityObjects", args[0]);
        } else {
            metaData.addProperty("sqlType", SqlType.UPDATE);
            metaData.addProperty("entityObjects", Collections.singleton(args[0]));
        }
        return metaData;
    }

    @Override
    public void check(TableInfo tableInfo, JdbcSession session, Object[] args) throws ExecutorException {
        super.check(tableInfo, session, args);
        if(args[0] == null)
            throw new ExecutorException("insert object is null");
        if(args[0] instanceof Collection && ((Collection)args[0]).isEmpty())
            throw new ExecutorException("insert object is empty");
    }

    @Override
    public RowSql buildRowSql(MetaData metaData) throws ExecutorException {
        RowSqlFactory rowSqlFactory = new InsertRowSqlFactory();
        return rowSqlFactory.getRowSql(metaData);
    }

    @Override
    public StatementWrapper openStatement(RowSql rowSql, JdbcSession session, TableInfo tableInfo, Object[] args) throws ExecutorException {
        try {
            Connections.DataSourceAndConnection dataSourceAndConnection = session.getTransaction().getDataSourceAndConnection();
            TransactionTask transactionTask = session.getTransaction().getTransactionTask();
            PreparedStatement preparedStatement;
            if (tableInfo.hasPk() && KeyStrategyManager.getPrimaryKeyStrategy(tableInfo.getPkColumn().getStrategyName()).getKeySelector() instanceof JdbcSelector)
                preparedStatement = dataSourceAndConnection.getConnection().prepareStatement(rowSql.getNativeSql(), Statement.RETURN_GENERATED_KEYS);
            else {
                if (tableInfo.hasPk()) {
                    KeyGenerator keyGenerator;
                    if ((keyGenerator = KeyStrategyManager.getPrimaryKeyStrategy(tableInfo.getPkColumn().getStrategyName()).getKeyGenerator()) instanceof NoKeyGenerator)
                        keyGenerator.nextKey(tableInfo, args[0]);
                }
                preparedStatement = dataSourceAndConnection.getConnection().prepareStatement(rowSql.getNativeSql());
            }
            StatementWrapper statementWrapper = new StatementWrapper(preparedStatement);
            statementWrapper.addProperty("dataSourceAndConnection", dataSourceAndConnection);
            statementWrapper.addProperty("transactionTask", transactionTask);
            return statementWrapper;
        } catch (SQLException e) {
            throw LogUtils.convertToAdaptLoggerException(e, rowSql.getNativeSql(), rowSql.getParams());
        }
    }

    @Override
    public Object runSql(StatementWrapper statementWrapper, RowSql rowSql) throws ExecutorException {
        Object result = super.runSql(statementWrapper, rowSql);
        TableInfo tableInfo = MetaWrapperUtils.of(statementWrapper, "tableInfo");
        Collection<?> entityObjects = MetaWrapperUtils.of(statementWrapper, "entityObjects");
        assert tableInfo != null : "tableInfo is null";
        if (tableInfo.hasPk()) {
            try {
                KeyStrategyManager.getPrimaryKeyStrategy(tableInfo.getPkColumn().getStrategyName()).getKeySelector().selectGeneratorKey(statementWrapper.getPrepareStatement(), tableInfo, entityObjects);
            } catch (ClassTypeException e) {
                throw new ExecutorException(e);
            }
        }
        return result;
    }

    @Override
    public SqlResult handleRunnerResult(Object result, TableInfo tableInfo, CacheKey cacheKey, ReturnTypeMapping returnTypeMapping) throws ExecutorException {
        return new SqlResult(SqlResult.CacheFlush.CLEAR, result, cacheKey);
    }
}
