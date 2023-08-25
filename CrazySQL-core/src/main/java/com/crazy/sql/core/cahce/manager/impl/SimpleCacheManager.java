package com.crazy.sql.core.cahce.manager.impl;

import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.cahce.impl.SimpleCache;
import com.crazy.sql.core.cahce.manager.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class SimpleCacheManager implements CacheManager {
    private static Logger logger=LoggerFactory.getLogger(SimpleCacheManager.class);
    private Map<String,Cache> cacheMap;
    private ScheduledExecutorService scheduledService= new ScheduledThreadPoolExecutor(10);
    private boolean enableAutoRegular=false;
    private final long clearInterval=60;
    private final int defaultCapacity;
    public SimpleCacheManager() {
        defaultCapacity=1000;
        cacheMap=Collections.synchronizedMap(new LinkedHashMap<String,Cache>(defaultCapacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String,Cache> eldest) {
                return size() > SimpleCacheManager.this.defaultCapacity;
            }
        });
    }
    public SimpleCacheManager(int capacity) {
        defaultCapacity=capacity;
        cacheMap=Collections.synchronizedMap(new LinkedHashMap<>(defaultCapacity));
    }
    @Override
    public <K,V> Cache<K, V> getCache(String name) {
        Cache<K, V> cache = this.cacheMap.get(name);
        if (cache == null||cache.isExpire()) {
            cache = createCache(name);
            this.cacheMap.put(name, cache);
        }
        logger.info("you get a cache and its name is "+name);
        return cache;
    }

    @Override
    public Set<String> keySet() {
        return cacheMap.entrySet().stream().filter((entry)->!entry.getValue().isExpire()).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    @Override
    public Collection<Cache> caches() {
        return cacheMap.values().stream().filter((cache)->!cache.isExpire()).collect(Collectors.toList());
    }

    @Override
    public void enableRegularClear(boolean enabled) {
        this.enableAutoRegular=enabled;
        if(enabled){
            scheduledService.schedule(()->{
                Set<Map.Entry<String,Cache>> entrySet=cacheMap.entrySet();
                ArrayList<String> keys=new ArrayList<>();
                for (Map.Entry<String,Cache> entry:entrySet
                     ) {
                    if(entry.getValue().isExpire())
                        keys.add(entry.getKey());
                }
                keys.forEach((key)->{
                    cacheMap.remove(key);
                });
            },clearInterval,TimeUnit.SECONDS);
        }else {
            scheduledService.shutdown();
            scheduledService=new ScheduledThreadPoolExecutor(10);
        }
    }


    @Override
    public boolean hasCache(String key) {
        return cacheMap.containsKey(key);
    }

    @Override
    public void clear() {
        logger.info("clear all cache");
        cacheMap.clear();
    }

    @Override
    public void destroy() {
        logger.info("All caches are about to be destroyed!");
        cacheMap.clear();
        scheduledService.shutdown();
        scheduledService=new ScheduledThreadPoolExecutor(10);
    }

    @Override
    public void remove(String key) {
        cacheMap.remove(key);
    }


    public <K,V> Cache<K,V> createCache(String name){
        logger.info("you create a cache and its name is "+name);
        return new SimpleCache<>(TimeUnit.HOURS,1);
    }
}
