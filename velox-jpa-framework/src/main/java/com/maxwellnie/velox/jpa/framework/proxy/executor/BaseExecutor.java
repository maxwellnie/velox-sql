package com.maxwellnie.velox.jpa.framework.proxy.executor;

import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.cahce.dirty.CacheDirtyManager;
import com.maxwellnie.velox.jpa.core.cahce.key.CacheKey;
import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.proxy.executor.Executor;
import com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper;
import com.maxwellnie.velox.jpa.framework.exception.ExecutorException;
import com.maxwellnie.velox.jpa.framework.proxy.executor.cycle.ExecuteCycle;
import com.maxwellnie.velox.jpa.framework.sql.SimpleSqlFragment;
import com.maxwellnie.velox.jpa.framework.utils.ErrorUtils;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper.*;

/**
 * 基本的对SQL方法执行器
 *
 * @author Maxwell Nie
 */
public abstract class BaseExecutor extends ExecuteCycle implements Executor {
    protected final Logger logger;

    public BaseExecutor(Logger logger, Object errorResult) {
        this.logger = logger;
        this.errorResult = errorResult;
    }

    /**
     * 所有的Executor都应该遵循这个规范。
     * @param tableInfo
     * @param context
     * @param cache
     * @param daoImplHashCode
     * @param args
     * @return
     */
    @Override
    public Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object, Object> cache, String daoImplHashCode, Object[] args) {
        try {
            checkArgs(args);
            Connection connection = checkContext(context, tableInfo);
            SimpleSqlFragment sqlFragment = getNativeSql(args, tableInfo);
            logger.debug("SQL ### : "+sqlFragment.getNativeSql());
            logger.debug("PARAM # : "+sqlFragment.getParams());
            StatementWrapper statementWrapper = openStatement(sqlFragment, connection, tableInfo, args);
            long startTime = System.currentTimeMillis();
            SqlResult sqlResult = executeSql(statementWrapper, sqlFragment, daoImplHashCode,cache);
            logger.debug("SQL EXECUTED | TIME: "+(System.currentTimeMillis() - startTime)+"ms.");
            flushCache(sqlResult, cache, context.getDirtyManager(), !context.getAutoCommit());
            return sqlResult.getResult();
        } catch (ExecutorException e) {
            logger.error(ErrorUtils.getSimpleExceptionLog(e));
            return errorResult;
        }
    }

    /**
     * 检查Jdbc环境。
     * @param jdbcContext
     * @param tableInfo
     * @return
     * @throws ExecutorException
     */
    protected Connection checkContext(JdbcContext jdbcContext, TableInfo tableInfo) throws ExecutorException {
        if (jdbcContext == null || tableInfo == null) {
            throw new ExecutorException("JdbcContext is null or tableInfo is null!");
        } else if (jdbcContext.isClosed()) {
            throw new ExecutorException("JdbcContext is closed!");
        } else {
            try {
                if (jdbcContext.getTransaction() == null) {
                    throw new ExecutorException("JdbcContext is not have Transaction!");
                } else {
                    Connection connection = jdbcContext.getTransaction().getConnection();
                    if (connection == null) {
                        throw new ExecutorException("Transaction cannot open Connection!");
                    } else
                        return connection;
                }
            } catch (SQLException e) {
                logger.error("The connection open failed\r\nmessage:" + e.getMessage() + "\r\ncause:" + e.getCause());
                throw new ExecutorException("Transaction cannot open Connection!");
            }
        }
    }

    /**
     * 检查方法参数。
     * @param args
     * @throws ExecutorException
     */
    protected abstract void checkArgs(Object[] args) throws ExecutorException;

    @Override
    protected void flushCache(SqlResult sqlResult, Cache cache, CacheDirtyManager dirtyManager, boolean isTransactional) throws ExecutorException {
        if (isTransactional && sqlResult != null && sqlResult.getCacheKey() != null && cache != null) {
            if (sqlResult.getFlag().equals(ExecuteCycle.FLUSH_FLAG)) {
                doFlushCache(sqlResult, cache, dirtyManager);
            } else {
                doClearCache(cache, dirtyManager);
            }
        }
    }

    /**
     * 更新缓存。
     * @param sqlResult
     * @param cache
     * @param dirtyManager
     */
    protected void doFlushCache(SqlResult sqlResult, Cache cache, CacheDirtyManager dirtyManager) {
        if (dirtyManager != null) {
            dirtyManager.get(cache).put(sqlResult.getCacheKey(), sqlResult.getResult());
        } else {
            cache.put(sqlResult.getCacheKey(), sqlResult.getResult());
        }
    }

    /**
     * 清理缓存。
     * @param cache
     * @param dirtyManager
     */
    protected void doClearCache(Cache<?, ?> cache, CacheDirtyManager dirtyManager) {
        if (dirtyManager != null) {
            dirtyManager.clear();
        } else {
            cache.clear();
        }
    }

    @Override
    protected StatementWrapper openStatement(SimpleSqlFragment sqlFragment, Connection connection, TableInfo tableInfo, Object[] args) throws ExecutorException {
        StatementWrapper statementWrapper;
        try {
            PreparedStatement statement = doOpenStatement(connection, tableInfo, sqlFragment.getNativeSql());
            statement.setFetchSize(tableInfo.getFetchSize());
            List<Object> params = sqlFragment.getParams();
            statementWrapper = new StatementWrapper(statement);
            statementWrapper.getMetaData().addProperty("tableInfo", tableInfo);
            doAfterOpenStatement(statementWrapper, params, args);
            CacheKey cacheKey=new CacheKey(tableInfo.getMappedClazz(), sqlFragment.getNativeSql(), null);
            cacheKey.addValueCollection(sqlFragment.getParams());
            statementWrapper.getMetaData().addProperty("cacheKey",cacheKey);
        } catch (SQLException e) {
            logger.error(ErrorUtils.getExceptionLog(e, sqlFragment.getNativeSql(), sqlFragment.getParams()));
            throw new ExecutorException("Statement open failed!");
        }
        return statementWrapper;
    }

    /**
     * 创建Statement对象，不同的使用场景将创建出不同的Statement。<br/>
     * 例如安全性考虑下将创建PrepareStatement以防止SQL注入。
     * @param connection
     * @param tableInfo
     * @param sql
     * @return
     * @throws SQLException
     */
    protected abstract PreparedStatement doOpenStatement(Connection connection,TableInfo tableInfo, String sql) throws SQLException;

    /**
     * 在Statement对象创建完成后，可以对prepareStatement进行设置值和修改值，对StatementWrapper添加数据，以便在执行SQL时能够使用到某些数据。
     * @param statementWrapper
     * @param params
     * @param args
     * @throws SQLException
     */
    protected abstract void doAfterOpenStatement(StatementWrapper statementWrapper, List<Object> params, Object[] args) throws SQLException;

    /**
     * 依据不同的模式执行不同的PreparedStatement的执行SQL操作
     * @param preparedStatement
     * @param mode 三种基础模式
     * @see StatementWrapper#BATCH
     * @see StatementWrapper#UPDATE
     * @see StatementWrapper#QUERY
     * @return
     * @throws SQLException
     * @throws ExecutorException
     */
    protected Object doExecuteSql(PreparedStatement preparedStatement, int mode) throws SQLException, ExecutorException {
        switch (mode) {
            case QUERY: return preparedStatement.executeQuery();
            case BATCH & UPDATE: return preparedStatement.executeBatch();
            case UPDATE:return preparedStatement.executeUpdate();
            default:throw new ExecutorException("Unsupported PreparedStatement Mode '"+mode+"'");
        }
    }
}