package com.maxwellnie.velox.jpa.framework.proxy.executor.cycle;

import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.cahce.dirty.CacheDirtyManager;
import com.maxwellnie.velox.jpa.core.cahce.key.CacheKey;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper;
import com.maxwellnie.velox.jpa.framework.exception.ExecutorException;
import com.maxwellnie.velox.jpa.framework.sql.SimpleSqlFragment;

import java.sql.Connection;

/**
 * 执行器的执行周期
 * @since 1.1
 * @author Maxwell Nie
 */
public abstract class ExecuteCycle {
    /**
     * 缓存清理标识。
     */
    public static final String CLEAR_FLAG = "1b4adf781a4ca21e";
    /**
     * 缓存更新标识。
     */
    public static final String FLUSH_FLAG = "3e5c6a74c1a9c3a1";
    protected Object errorResult = 0;

    /**
     * 创建Sql阶段。
     * @param args
     * @param tableInfo
     * @return
     * @throws ExecutorException
     */
    protected abstract SimpleSqlFragment getNativeSql(Object[] args, TableInfo tableInfo) throws ExecutorException;

    /**
     * 实例化Statement阶段。
     * @param sqlFragment
     * @param connection
     * @param tableInfo
     * @param args
     * @return
     * @throws ExecutorException
     */
    protected abstract StatementWrapper openStatement(SimpleSqlFragment sqlFragment, Connection connection, TableInfo tableInfo, Object[] args) throws ExecutorException;

    /**
     * 执行Sql阶段。
     * @param statementWrapper
     * @param sqlFragment
     * @param daoImplHashCode
     * @param cache
     * @return
     * @throws ExecutorException
     */
    protected abstract SqlResult executeSql(StatementWrapper statementWrapper, SimpleSqlFragment sqlFragment, String daoImplHashCode, Cache<Object,Object> cache) throws ExecutorException;

    /**
     * 刷新缓存阶段。
     * @param sqlResult
     * @param cache
     * @param dirtyManager
     * @param isTransactional
     * @throws ExecutorException
     */
    protected abstract void flushCache(SqlResult sqlResult, Cache cache, CacheDirtyManager dirtyManager, boolean isTransactional) throws ExecutorException;

    /**
     * Sql执行的返回结果（包装器）。
     */
    public static class SqlResult {
        /**
         * 缓存工作标识。
         */
        private String flag;
        /**
         * sql执行结果。
         */
        private Object result;
        /**
         * 缓存的键。
         */
        private CacheKey cacheKey;

        public SqlResult() {
        }

        public SqlResult(String flag, Object result, CacheKey cacheKey) {
            this.flag = flag;
            this.result = result;
            this.cacheKey = cacheKey;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
        }

        public CacheKey getCacheKey() {
            return cacheKey;
        }

        public void setCacheKey(CacheKey cacheKey) {
            this.cacheKey = cacheKey;
        }
    }
}
