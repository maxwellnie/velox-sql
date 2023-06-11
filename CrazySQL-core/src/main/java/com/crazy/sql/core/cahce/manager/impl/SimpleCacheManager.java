package com.crazy.sql.core.cahce.manager.impl;

import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.cahce.impl.SimpleCache;
import com.crazy.sql.core.cahce.manager.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class SimpleCacheManager implements CacheManager {
    private static Logger logger=LoggerFactory.getLogger(SimpleCacheManager.class);
    private Map<String,Cache> cacheMap= new ConcurrentHashMap<>();
    private Map<String,Future<String>> futures=new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduledService= new ScheduledThreadPoolExecutor(10);
    private boolean enableAutoRegular=true;
    private long expireTime=1200000;
    private TimeUnit timeUnit=TimeUnit.MILLISECONDS;
    @Override
    public <K,V> Cache<K, V> getCache(String name) {
        Cache<K, V> cache = this.cacheMap.get(name);
        if (cache == null) {
            cache = createCache(name);
            this.cacheMap.put(name, cache);
        }
        logger.info("you get a cache and its name is "+name);
        return cache;
    }

    @Override
    public Set<String> keySet() {
        return cacheMap.keySet();
    }

    @Override
    public Collection<Cache> caches() {
        return cacheMap.values();
    }

    @Override
    public void setExpireTime(long time,TimeUnit timeUnit) {
        this.expireTime=time;
    }

    @Override
    public long getExpireTime() {
        return this.expireTime;
    }

    @Override
    public void enableRegularClear() {
        this.enableAutoRegular=true;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return this.timeUnit;
    }

    @Override
    public boolean hasCache(String key) {
        return cacheMap.containsKey(key);
    }

    @Override
    public void clear() {
        logger.info("clear all cache");
        cacheMap.clear();
        futures.forEach((x,y)->{y.cancel(true);});
        futures.clear();
    }

    @Override
    public void destroy() {
        logger.info("All caches are about to be destroyed!");
        futures.forEach((x,y)->{y.cancel(true);});
        cacheMap.clear();
        scheduledService.shutdown();
        scheduledService=new ScheduledThreadPoolExecutor(10);
    }

    @Override
    public void remove(String key) {
        cacheMap.remove(key);
        futures.remove(key);
    }

    public <K,V> Cache<K,V> createCache(String name){
        logger.info("you create a cache and its name is "+name);
        Cache<K,V> cache=new SimpleCache<>();
        if(enableAutoRegular) {
            Future<String> future=scheduledService.schedule(() -> {
                remove(name);
                return "complete";
            },expireTime,timeUnit);
            futures.put(name,future);
        }
        return cache;
    }
}
