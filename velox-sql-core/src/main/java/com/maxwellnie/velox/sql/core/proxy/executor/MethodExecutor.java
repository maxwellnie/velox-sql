package com.maxwellnie.velox.sql.core.proxy.executor;

import com.maxwellnie.velox.sql.core.cache.Cache;
import com.maxwellnie.velox.sql.core.cache.key.CacheKey;
import com.maxwellnie.velox.sql.core.cache.transactional.CacheTransactional;
import com.maxwellnie.velox.sql.core.meta.MetaData;
import com.maxwellnie.velox.sql.core.natives.exception.ExecutorException;
import com.maxwellnie.velox.sql.core.natives.jdbc.context.Context;
import com.maxwellnie.velox.sql.core.natives.jdbc.mapping.ReturnTypeMapping;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSession;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.row.RowSql;
import com.maxwellnie.velox.sql.core.natives.jdbc.statement.StatementWrapper;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfo;
import com.maxwellnie.velox.sql.core.proxy.executor.result.SqlResult;
import org.slf4j.Logger;

/**
 * 方法执行器，执行被代理的方法
 *
 * @author Maxwell Nie
 */
public interface MethodExecutor {
    /**
     * 执行
     *
     * @param tableInfo
     * @param session
     * @param cache
     * @param daoImplHashCode
     * @param returnTypeMapping
     * @param args
     * @return 操作结果
     */
    Object execute(TableInfo tableInfo, JdbcSession session, Cache<Object, Object> cache, String daoImplHashCode, ReturnTypeMapping returnTypeMapping, Object[] args);

    /**
     * 检查参数
     *
     * @param tableInfo
     * @param session
     * @param args
     * @throws ExecutorException
     */
    void check(TableInfo tableInfo, JdbcSession session, Object[] args) throws ExecutorException;

    /**
     * 对所需要的数据进行预处理。
     *
     * @param tableInfo
     * @param args
     * @return 元数据
     * @throws ExecutorException
     */
    MetaData prepared(TableInfo tableInfo, Object[] args) throws ExecutorException;

    /**
     * 构建sql
     *
     * @param metaData
     * @return sql
     * @throws ExecutorException
     */
    RowSql buildRowSql(MetaData metaData) throws ExecutorException;

    /**
     * 打开statement
     *
     * @param rowSql
     * @param session
     * @param tableInfo
     * @param args
     * @return statement
     * @throws ExecutorException
     */
    StatementWrapper openStatement(RowSql rowSql, JdbcSession session, TableInfo tableInfo, Object[] args) throws ExecutorException;

    /**
     * 执行sql
     *
     * @param statementWrapper
     * @param rowSql
     * @return sql结果
     * @throws ExecutorException
     */
    Object runSql(StatementWrapper statementWrapper, RowSql rowSql) throws ExecutorException;

    /**
     * 处理runner执行结果
     *
     * @param result
     * @param tableInfo
     * @param cacheKey
     * @param returnTypeMapping
     * @return sql结果
     * @throws ExecutorException
     */
    SqlResult handleRunnerResult(Object result, TableInfo tableInfo, CacheKey cacheKey, ReturnTypeMapping returnTypeMapping) throws ExecutorException;

    /**
     * 刷新缓存
     *
     * @param sqlResult
     * @param cache
     * @param dirtyManager
     * @param isTransactional
     * @throws ExecutorException
     */
    void flushCache(SqlResult sqlResult, Cache cache, CacheTransactional dirtyManager, boolean isTransactional) throws ExecutorException;

    /**
     * 关闭statement
     *
     * @param statementWrapper
     * @throws ExecutorException
     */
    void closeStatement(StatementWrapper statementWrapper) throws ExecutorException;

    Logger getLogger();

    void setMethodMappedManager(Context.MethodMappedManager methodMappedManager);
}
