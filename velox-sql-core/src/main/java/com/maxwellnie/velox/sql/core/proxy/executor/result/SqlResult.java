package com.maxwellnie.velox.sql.core.proxy.executor.result;

import com.maxwellnie.velox.sql.core.cache.key.CacheKey;

/**
 * @author Maxwell Nie
 * Sql执行的返回结果（包装器）。
 */
public class SqlResult {
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

    public interface CacheFlush {
        String FLUSH = "flush";
        String CLEAR = "clear";
    }
}
