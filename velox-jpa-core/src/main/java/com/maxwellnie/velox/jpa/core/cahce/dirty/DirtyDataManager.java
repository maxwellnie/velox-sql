package com.maxwellnie.velox.jpa.core.cahce.dirty;

import com.maxwellnie.velox.jpa.core.cahce.key.CacheKey;

/**
 * @author Maxwell Nie
 */
public interface DirtyDataManager {
    void put(CacheKey key, Object value);

    void clear();

    void commit();

    void rollback();
}
