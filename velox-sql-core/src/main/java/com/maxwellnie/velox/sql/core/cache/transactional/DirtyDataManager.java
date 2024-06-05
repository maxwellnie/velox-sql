package com.maxwellnie.velox.sql.core.cache.transactional;

import com.maxwellnie.velox.sql.core.cache.key.CacheKey;

/**
 * @author Maxwell Nie
 */
public interface DirtyDataManager {
    void put(CacheKey key, Object value);

    void clear();

    void commit();

    void rollback();
}
