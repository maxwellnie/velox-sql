package com.maxwellnie.vleox.jpa.core.proxy.executor.impl;

import com.maxwellnie.vleox.jpa.core.cahce.Cache;
import com.maxwellnie.vleox.jpa.core.cahce.dirty.CacheDirtyManager;
import com.maxwellnie.vleox.jpa.core.cahce.key.CacheKey;
import com.maxwellnie.vleox.jpa.core.proxy.executor.Executor;

/**
 * @author Maxwell Nie
 */
public abstract class BaseCacheExecutor implements Executor {
    protected abstract void flushCache(Object result, CacheKey cacheKey, Cache cache, CacheDirtyManager cacheDirtyManager, boolean isTransactional);
}
