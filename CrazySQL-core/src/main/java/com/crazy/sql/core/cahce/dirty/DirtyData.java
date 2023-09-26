package com.crazy.sql.core.cahce.dirty;

import com.crazy.sql.core.cahce.key.CacheKey;

/**
 * @author Akiba no ichiichiyoha
 */
public class DirtyData {
    private CacheKey key;
    private Object data;

    public DirtyData() {
    }

    public DirtyData(CacheKey key, Object data) {
        this.key = key;
        this.data = data;
    }

    public CacheKey getKey() {
        return key;
    }

    public void setKey(CacheKey key) {
        this.key = key;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
