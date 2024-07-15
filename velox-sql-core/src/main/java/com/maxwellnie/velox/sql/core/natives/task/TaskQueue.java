package com.maxwellnie.velox.sql.core.natives.task;

import com.maxwellnie.velox.sql.core.cache.key.CacheKey;

import java.io.Serializable;

/**
 * @author Maxwell Nie
 */
public interface TaskQueue extends Serializable {
    void require(String group, CacheKey cacheKey, Runnable task);
}
