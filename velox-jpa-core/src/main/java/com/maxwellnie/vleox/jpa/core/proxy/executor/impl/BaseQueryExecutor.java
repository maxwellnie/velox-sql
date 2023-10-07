package com.maxwellnie.vleox.jpa.core.proxy.executor.impl;

import com.maxwellnie.vleox.jpa.core.cahce.Cache;
import com.maxwellnie.vleox.jpa.core.cahce.dirty.CacheDirtyManager;
import com.maxwellnie.vleox.jpa.core.cahce.key.CacheKey;
import com.maxwellnie.vleox.jpa.core.dao.support.SqlBuilder;
import com.maxwellnie.vleox.jpa.core.jdbc.table.TableInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author Maxwell Nie
 */
public abstract class BaseQueryExecutor extends BaseCacheExecutor {
    protected abstract Map<String, Object> openStatement(Connection connection, TableInfo tableInfo, SqlBuilder sqlBuilder, Cache<Object, Object> cache, String daoImplHashCode, CacheDirtyManager cacheDirtyManager) throws SQLException;

    @Override
    protected void flushCache(Object result, CacheKey cacheKey, Cache cache, CacheDirtyManager cacheDirtyManager, boolean isTransactional) {
        if (cache != null && result != null) {
            if (isTransactional) {
                cacheDirtyManager.get(cache).put(cacheKey, result);
            } else {
                cache.put(cacheKey, result);
            }
        }
    }
}
