package com.crazy.sql.core.cahce.dirty;

import com.crazy.sql.core.cahce.key.CacheKey;

/**
 * @author Akiba no ichiichiyoha
 */
public interface DirtyDataManager {
    void put(CacheKey key, Object value);
    void clear();
    void commit();
    void rollback();
}
