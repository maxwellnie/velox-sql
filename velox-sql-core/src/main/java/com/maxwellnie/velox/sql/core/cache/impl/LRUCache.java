package com.maxwellnie.velox.sql.core.cache.impl;

import com.maxwellnie.velox.sql.core.cache.Cache;
import com.maxwellnie.velox.sql.core.natives.concurrent.ThreadLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.Lock;

/**
 * LRU算法的缓存
 */
public class LRUCache implements Cache<Object, Object> {
    private static final Logger logger = LoggerFactory.getLogger(LRUCache.class);
    private int capacity = 100;
    private static final Lock READ_LOCK = ThreadLock.readLock("velox.sql.cache.LRUCache");
    private static final Lock WRITE_LOCK = ThreadLock.writeLock("velox.sql.cache.LRUCache");
    private final Map<Object, Object> data = new LinkedHashMap<Object, Object>(capacity, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
            return size() > LRUCache.this.getCapacity();
        }
    };

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
        try{
            WRITE_LOCK.lock();
            logger.debug("put cache named " + k);
            data.put(k, v);
        }finally {
            WRITE_LOCK.unlock();
        }
    }

    @Override
    public Object get(Object k) {
        try {
            READ_LOCK.lock();
            logger.debug("select cache named " + k);
            return data.get(k);
        }finally {
            READ_LOCK.unlock();
        }
    }

    @Override
    public Object remove(Object k) {
        try {
            WRITE_LOCK.lock();
            logger.debug(k+" is removed.");
            return data.remove(k);
        }finally {
            WRITE_LOCK.unlock();
        }
    }

    @Override
    public int size() {
        try {
            READ_LOCK.lock();
            return data.size();
        }finally {
            READ_LOCK.unlock();
        }
    }

    @Override
    public Set<Object> keys() {
        try {
            READ_LOCK.lock();
            return data.keySet();
        }finally {
            READ_LOCK.unlock();
        }
    }

    @Override
    public Collection<Object> values() {
        try {
            READ_LOCK.lock();
            return data.values();
        }finally {
            READ_LOCK.unlock();
        }
    }

    @Override
    public void clear() {
        try {
            WRITE_LOCK.lock();
            logger.debug(super.toString()+" is cleared.");
            data.clear();
        }finally {
            WRITE_LOCK.unlock();
        }
    }

    @Override
    public String toString() {
        return "LRUCache{" +
                "data=" + data +
                '}';
    }
}
