package com.crazy.sql.core.utils;

import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.jdbc.AutoCallBackConnection;
import com.crazy.sql.core.jdbc.dirtydata.DirtyData;
import com.crazy.sql.core.jdbc.dirtydata.cache.impl.ConnectionDirtyCache;

import java.util.List;
import java.util.Map;

import static com.crazy.sql.core.jdbc.dirtydata.cache.impl.ConnectionDirtyCache.*;

public class DirtyUtils {
    public static void writeCache(CacheManager cacheManager, ConnectionDirtyCache cache){
        List<DirtyData> dataList=cache.getDirtyData();

        dataList.stream().forEach((x)->{
                switch (x.getMode()){
                    case INSERT:;break;
                    case UPDATE:;break;
                    case DELETE:;break;
                    default:;
                }
            }
        );
    }
    public static void putInsertDirty(AutoCallBackConnection connection,int primaryKeyValue,Object data){
        ConnectionDirtyCache cache=connection.getCache();
        cache.add(primaryKeyValue, INSERT,data);
    }
    public static void putUpdateDirty(AutoCallBackConnection connection,int primaryKeyValue,Object data){
        ConnectionDirtyCache cache=connection.getCache();
        cache.add(primaryKeyValue,ConnectionDirtyCache.UPDATE,data);
    }
    public static void putDeleteDirty(AutoCallBackConnection connection,int primaryKeyValue,Object data){
        ConnectionDirtyCache cache=connection.getCache();
        cache.add(primaryKeyValue,ConnectionDirtyCache.DELETE,data);
    }
}
