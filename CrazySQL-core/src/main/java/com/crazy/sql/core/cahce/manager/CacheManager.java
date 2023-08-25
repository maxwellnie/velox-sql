package com.crazy.sql.core.cahce.manager;

import com.crazy.sql.core.cahce.Cache;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface CacheManager{
    <K,V> Cache<K,V> getCache(String name);
    Set<String> keySet();
    Collection<Cache> caches();
    void enableRegularClear(boolean enabled);
    boolean hasCache(String key);
    void clear();
    void destroy();
    void remove(String key);
}
