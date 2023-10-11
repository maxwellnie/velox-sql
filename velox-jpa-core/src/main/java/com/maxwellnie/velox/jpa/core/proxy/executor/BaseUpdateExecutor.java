package com.maxwellnie.velox.jpa.core.proxy.executor;

import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.cahce.dirty.CacheDirtyManager;
import com.maxwellnie.velox.jpa.core.cahce.key.CacheKey;

/**
 * @author Maxwell Nie
 */
public abstract class BaseUpdateExecutor extends BaseCacheExecutor {
    @Override
    protected void flushCache(Object result, CacheKey cacheKey, Cache cache, CacheDirtyManager cacheDirtyManager, boolean isTransactional) {
        if (cache != null) {
            if (isTransactional) {
                cacheDirtyManager.get(cache).clear();
            } else {
                cache.clear();
            }
        }
    }
}
