package com.maxwellnie.velox.sql.core.cache.impl;

import com.maxwellnie.velox.sql.core.cache.Cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Maxwell Nie
 */
public class ComponentClassicCache implements Cache<Object, Object> {
    private final Map<Object, Object> cache;

    public ComponentClassicCache() {
        this.cache = new HashMap<>();
    }

    public ComponentClassicCache(Map<Object, Object> cache) {
        this.cache = cache;
    }

    @Override
    public void put(Object o, Object o2) {
        cache.put(o, o2);
    }

    @Override
    public Object get(Object o) {
        return cache.get(o);
    }

    @Override
    public Object remove(Object o) {
        return cache.remove(o);
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public Set<Object> keys() {
        return cache.keySet();
    }

    @Override
    public Collection<Object> values() {
        return cache.values();
    }

    @Override
    public void clear() {
        cache.clear();
    }
}
