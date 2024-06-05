package com.maxwellnie.velox.sql.core.proxy.executor.impl;

import com.maxwellnie.velox.sql.core.cache.Cache;
import com.maxwellnie.velox.sql.core.cache.transactional.CacheTransactional;
import com.maxwellnie.velox.sql.core.cache.key.CacheKey;
import com.maxwellnie.velox.sql.core.meta.MetaData;
import com.maxwellnie.velox.sql.core.natives.exception.ClassTypeException;
import com.maxwellnie.velox.sql.core.natives.exception.ExecutorException;
import com.maxwellnie.velox.sql.core.natives.exception.MethodNotSupportException;
import com.maxwellnie.velox.sql.core.natives.jdbc.context.Context;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSession;
import com.maxwellnie.velox.sql.core.natives.jdbc.mapping.ReturnTypeMapping;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlType;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.row.RowSql;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.runner.SqlExecutor;
import com.maxwellnie.velox.sql.core.natives.jdbc.statement.StatementWrapper;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfo;
import com.maxwellnie.velox.sql.core.natives.task.TaskQueue;
import com.maxwellnie.velox.sql.core.proxy.executor.MethodExecutor;
import com.maxwellnie.velox.sql.core.proxy.executor.result.SqlResult;
import com.maxwellnie.velox.sql.core.utils.common.MetaWrapperUtils;
import com.maxwellnie.velox.sql.core.utils.common.SystemClock;
import com.maxwellnie.velox.sql.core.utils.log.LogUtils;
import org.slf4j.Logger;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Maxwell Nie
 */
public abstract class BaseMethodExecutor implements MethodExecutor {
    private final Logger logger;
    protected Context.MethodMappedManager methodMappedManager;

    public BaseMethodExecutor(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Object execute(TableInfo tableInfo, JdbcSession session, Cache<Object, Object> cache, String daoImplHashCode, ReturnTypeMapping returnTypeMapping, Object[] args) {
       throw new MethodNotSupportException("You need use cycle.");
    }

    @Override
    public void check(TableInfo tableInfo, JdbcSession session, Object[] args) throws ExecutorException {
        if (session == null) {
            throw new ExecutorException("JdbcSession is null!");
        }else if(tableInfo == null){
            throw new ExecutorException("TableInfo is null!");
        }
        else if (session.isClosed()) {
            throw new ExecutorException("JdbcSession is closed!");
        } else {
            if (session.getTransaction() == null) {
                throw new ExecutorException("JdbcSession is not have Transaction!");
            }
        }
    }
    @Override
    public StatementWrapper openStatement(RowSql rowSql, JdbcSession session, TableInfo tableInfo, Object[] args) throws ExecutorException {
        try {
            PreparedStatement preparedStatement = session.getTransaction().getConnection().prepareStatement(rowSql.getNativeSql());
            return new StatementWrapper(preparedStatement);
        } catch (SQLException e) {
            throw LogUtils.convertToAdaptLoggerException(e, rowSql.getNativeSql(), rowSql.getParams());
        }
    }

    @Override
    public Object runSql(StatementWrapper statementWrapper, RowSql rowSql) throws ExecutorException {
        SqlExecutor<?> sqlExecutor = SqlExecutor.get(rowSql.getSqlType());
        try {
            return sqlExecutor.run(rowSql, statementWrapper);
        } catch (SQLException | ClassTypeException e) {
            throw LogUtils.convertToAdaptLoggerException(e, rowSql.getNativeSql(), rowSql.getParams());
        }
    }
    @Override
    public void flushCache(SqlResult sqlResult, Cache cache, CacheTransactional dirtyManager, boolean isTransactional) throws ExecutorException {
        if (sqlResult != null && sqlResult.getCacheKey() != null && cache != null) {
            if (sqlResult.getFlag().equals(SqlResult.CacheFlush.FLUSH)) {
                logger.debug("Cache flushed.");
                doFlushCache(sqlResult, cache, dirtyManager, isTransactional);
            } else {
                logger.debug("Cache cleared.");
                doClearCache(cache, dirtyManager, isTransactional);
            }
        }
    }
    /**
     * 更新缓存。
     *
     * @param sqlResult
     * @param cache
     * @param dirtyManager
     */
    protected void doFlushCache(SqlResult sqlResult, Cache cache, CacheTransactional dirtyManager, boolean isTransactional) {
        if (dirtyManager != null && isTransactional) {
            dirtyManager.get(cache).put(sqlResult.getCacheKey(), sqlResult.getResult());
        } else {
            cache.put(sqlResult.getCacheKey(), sqlResult.getResult());
        }
    }
    /**
     * 清理缓存。
     *
     * @param cache
     * @param dirtyManager
     */
    protected void doClearCache(Cache<?, ?> cache, CacheTransactional dirtyManager, boolean isTransactional) {
        if (dirtyManager != null && isTransactional) {
            dirtyManager.clear();
        }
        cache.clear();
    }
    @Override
    public void closeStatement(StatementWrapper statementWrapper) throws ExecutorException {
        try {
            statementWrapper.get().close();
            logger.debug("Statement [{}] closed.", statementWrapper.get());
        } catch (SQLException e) {
            throw new ExecutorException(e);
        }
    }

    public Logger getLogger() {
        return logger;
    }

    @Override
    public void setMethodMappedManager(Context.MethodMappedManager methodMappedManager) {
        this.methodMappedManager = methodMappedManager;
    }
}
