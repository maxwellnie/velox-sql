package com.crazy.sql.core.cahce.impl;

import com.crazy.sql.core.cahce.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleCache<K,V> implements Cache<K,V> {
    private static Logger logger= LoggerFactory.getLogger(SimpleCache.class);
    private ConcurrentHashMap<K,V> data=new ConcurrentHashMap<>();
    @Override
    public void put(K k, V v) {
        data.put(k,v);
    }

    @Override
    public V get(K k) {
        logger.info("Cache hit");
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
}
