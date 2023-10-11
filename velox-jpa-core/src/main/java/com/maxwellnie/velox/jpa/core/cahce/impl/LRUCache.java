package com.maxwellnie.velox.jpa.core.cahce.impl;

import com.maxwellnie.velox.jpa.core.cahce.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class LRUCache implements Cache<Object, Object> {
    private static final Logger logger = LoggerFactory.getLogger(LRUCache.class);
    private int capacity = 100;
    private final Map<Object, Object> data = Collections.synchronizedMap(new LinkedHashMap<Object, Object>(capacity, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
            return size() > LRUCache.this.getCapacity();
        }
    });

    public LRUCache() {
    }

    public LRUCache(int capacity) {
        this.capacity = capacity;
    }

    public long getCapacity() {
        return capacity;
    }

    @Override
    public void put(Object k, Object v) {
        logger.debug("put cache named " + k);
        data.put(k, v);
    }

    @Override
    public Object get(Object k) {
        logger.debug("select cache named " + k);
        return data.get(k);
    }

    @Override
    public Object remove(Object k) {
        return data.remove(k);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public Set<Object> keys() {
        return data.keySet();
    }

    @Override
    public Collection<Object> values() {
        return data.values();
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public String toString() {
        return "LRUCache{" +
                "data=" + data +
                '}';
    }
}
