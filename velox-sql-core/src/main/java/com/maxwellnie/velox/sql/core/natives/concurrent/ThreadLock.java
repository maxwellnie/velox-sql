package com.maxwellnie.velox.sql.core.natives.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 这是一个框架的共享读写锁的映射集，用于处理框架单一共享资源的问题。
 * @author Maxwell Nie
 */
public class ThreadLock {
    /**
     * 共享资源的映射集
     */
    private static final Map<String, ReentrantReadWriteLock> INSTANCE = new HashMap<>();
    /**
     * 核心共享资源
     */
    public static final String CORE = "CORE";
    private ThreadLock(){
        INSTANCE.put(CORE, new ReentrantReadWriteLock());
    }
    /**
     * 获取共享资源的读写锁
     * @param key 共享资源的key
     * @return 共享资源的读写锁
     */
    public static ReentrantReadWriteLock reentrantReadWriteLock(String key){
        synchronized (INSTANCE){
            if (INSTANCE.containsKey(key))
                return INSTANCE.get(key);
            else{
                ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
                INSTANCE.put(key, reentrantReadWriteLock);
                return reentrantReadWriteLock;
            }
        }
    }
    /**
     * 获取共享资源的读锁
     * @param key 共享资源的key
     * @return 共享资源的读锁
     */
    public static ReentrantReadWriteLock.ReadLock readLock(String key){
        return reentrantReadWriteLock(key).readLock();
    }
    /**
     * 获取共享资源的写锁
     * @param key 共享资源的key
     * @return 共享资源的写锁
     */
    public static ReentrantReadWriteLock.WriteLock writeLock(String key){
        return reentrantReadWriteLock(key).writeLock();
    }
}
