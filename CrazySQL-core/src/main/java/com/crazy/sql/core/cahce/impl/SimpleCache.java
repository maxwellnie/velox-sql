package com.crazy.sql.core.cahce.impl;

import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.cahce.manager.impl.SimpleCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class SimpleCache<K,V> implements Cache<K,V> {
    private static final Logger logger= LoggerFactory.getLogger(SimpleCache.class);
    private final Map<K,V> data=new ConcurrentHashMap<>();
    private TimeUnit timeUnit;
    private long expireTime;
    private final long createTime;

    public SimpleCache(TimeUnit timeUnit, long expireTime) {
        this.createTime=System.currentTimeMillis();
        this.timeUnit = timeUnit;
        this.expireTime = expireTime;
    }

    @Override
    public void put(K k, V v) {
        data.put(k,v);
    }

    @Override
    public V get(K k) {
        logger.info("Cache hit");
        logger.info(toString());
        return data.get(k);
    }

    @Override
    public V remove(K k) {
        return data.remove(k);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public Set<K> keys() {
        return data.keySet();
    }

    @Override
    public Collection<V> values() {
        logger.info("Cache hit");
        return data.values();
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public void setExpireTime(TimeUnit timeUnit, long expireTime) {
        this.timeUnit=timeUnit;
        this.expireTime=expireTime;
    }

    @Override
    public boolean isExpire() {
        return timeUnit.toMillis(expireTime)<=(System.currentTimeMillis()-createTime);
    }

    @Override
    public long getExpireTime() {
        return this.expireTime;
    }

    @Override
    public String toString() {
        return "SimpleCache{" +
                "data=" + data +
                '}';
    }
}
