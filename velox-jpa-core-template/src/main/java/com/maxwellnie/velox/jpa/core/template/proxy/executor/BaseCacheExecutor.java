package com.maxwellnie.velox.jpa.core.template.proxy.executor;

import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.cahce.dirty.CacheDirtyManager;
import com.maxwellnie.velox.jpa.core.cahce.key.CacheKey;
import com.maxwellnie.velox.jpa.core.proxy.executor.Executor;

/**
 * @author Maxwell Nie
 */
public abstract class BaseCacheExecutor implements Executor {
    protected abstract void flushCache(Object result, CacheKey cacheKey, Cache cache, CacheDirtyManager cacheDirtyManager, boolean isTransactional);
}