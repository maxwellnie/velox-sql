package com.crazy.sql.core.utils;

import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.cahce.manager.CacheManager;
import java.util.List;

public class CacheUtils {
    public static <T> void updateCache(CacheManager cacheManager, SQLUtils<T> sqlUtils,List<T> data){
        if(cacheManager.hasCache(sqlUtils.getTableName()+"queryAll")) {
            cacheManager.refreshExpireTime(sqlUtils.getTableName()+"queryAll");
            Cache<String,T> queryAllCache=cacheManager.getCache(sqlUtils.getTableName()+"queryAll");
            for (T t:data) {
                queryAllCache.put(sqlUtils.getPrimaryKeyValue(t),t);
            }
        }
        if(cacheManager.hasCache(sqlUtils.getTableName()+"queryOne")) {
            cacheManager.refreshExpireTime(sqlUtils.getTableName()+"queryOne");
            Cache<String, T> queryOneCache = cacheManager.getCache(sqlUtils.getTableName() + "queryOne");
            for (T t : data) {
                queryOneCache.put(sqlUtils.getPrimaryKeyValue(t), t);
            }
        }
        cacheManager.keySet().stream().filter(x->x.matches(sqlUtils.getTableName()+"queryByWords"+"(.*)")).forEach(cacheManager::remove);
    }
    public static <T> void deleteCache(CacheManager cacheManager, SQLUtils<T> sqlUtils,List<String> keys){
        if(cacheManager.hasCache(sqlUtils.getTableName()+"queryAll")) {
            Cache<String,T> queryAllCache=cacheManager.getCache(sqlUtils.getTableName()+"queryAll");
            for (String s:keys) {
                queryAllCache.remove(s);
            }
        }
        if(cacheManager.hasCache(sqlUtils.getTableName()+"queryOne")) {
            Cache<String, T> queryOneCache = cacheManager.getCache(sqlUtils.getTableName() + "queryOne");
            for (String s:keys) {
                queryOneCache.remove(s);
            }
        }
        cacheManager.keySet().stream().filter(x->x.matches(sqlUtils.getTableName()+"queryByWords"+"(.*)")).forEach(cacheManager::remove);
    }
}