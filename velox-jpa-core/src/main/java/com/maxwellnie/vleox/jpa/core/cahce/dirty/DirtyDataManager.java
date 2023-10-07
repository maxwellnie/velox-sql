package com.maxwellnie.vleox.jpa.core.cahce.dirty;

import com.maxwellnie.vleox.jpa.core.cahce.key.CacheKey;

/**
 * @author Maxwell Nie
 */
public interface DirtyDataManager {
    void put(CacheKey key, Object value);

    void clear();

    void commit();

    void rollback();
}
