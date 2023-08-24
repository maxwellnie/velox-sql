package com.crazy.sql.core.utils;

import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.cahce.manager.CacheManager;
import java.util.List;

public class CacheUtils {
    public static void updateCache(CacheManager cacheManager, SQLUtils sqlUtils,List data){
        if(cacheManager.hasCache(sqlUtils.getTableName()+"queryAll")) {
            cacheManager.refreshExpireTime(sqlUtils.getTableName()+"queryAll");
            Cache queryAllCache=cacheManager.getCache(sqlUtils.getTableName()+"queryAll");
            for (Object t:data) {
                queryAllCache.put(sqlUtils.getPrimaryKeyValue(t),t);
            }
        }
        if(cacheManager.hasCache(sqlUtils.getTableName()+"queryOne")) {
            cacheManager.refreshExpireTime(sqlUtils.getTableName()+"queryOne");
            Cache queryOneCache = cacheManager.getCache(sqlUtils.getTableName() + "queryOne");
            for (Object t : data) {
                queryOneCache.put(sqlUtils.getPrimaryKeyValue(t), t);
            }
        }
        cacheManager.keySet().stream().filter(x->x.matches(sqlUtils.getTableName()+"queryByWords"+"(.*)")).forEach(cacheManager::remove);
    }
    public static void deleteCache(CacheManager cacheManager, SQLUtils sqlUtils,List<String> keys){
        if(cacheManager.hasCache(sqlUtils.getTableName()+"queryAll")) {
            Cache queryAllCache=cacheManager.getCache(sqlUtils.getTableName()+"queryAll");
            for (String s:keys) {
                queryAllCache.remove(s);
            }
        }
        if(cacheManager.hasCache(sqlUtils.getTableName()+"queryOne")) {
            Cache queryOneCache = cacheManager.getCache(sqlUtils.getTableName() + "queryOne");
            for (String s:keys) {
                queryOneCache.remove(s);
            }
        }
        cacheManager.keySet().stream().filter(x->x.matches(sqlUtils.getTableName()+"queryByWords"+"(.*)")).forEach(cacheManager::remove);
    }
}