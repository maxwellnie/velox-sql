package com.crazy.sql.core.utils;

import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.jdbc.EnableTransactionConnection;
import com.crazy.sql.core.jdbc.dirtydata.DirtyData;
import com.crazy.sql.core.jdbc.dirtydata.queue.ConnectionDirtyDataQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;

import static com.crazy.sql.core.jdbc.dirtydata.queue.ConnectionDirtyDataQueue.*;

public class DirtyDataUtils {
    private static Logger logger= LoggerFactory.getLogger(DirtyDataUtils.class);
    public static void writeCache(CacheManager cacheManager, ConnectionDirtyDataQueue cache){
        logger.info("write dirty data to cache");
        List<DirtyData> dataList=cache.getDirtyData();

        dataList.stream().forEach((x)->{
                switch (x.getMode()){
                    case INSERT:
                    case UPDATE:
                        CacheUtils.updateCache(cacheManager,x.getSqlUtils(), Collections.singletonList(x.getData()));break;
                    case DELETE:CacheUtils.deleteCache(cacheManager,x.getSqlUtils(), Collections.singletonList((String) x.getData()));break;
                    default:;
                }
            }
        );
    }
    public static void putInsertDirty(Connection connection, String primaryKeyValue, Object data, long row, SQLUtils sqlUtils){
        if(connection instanceof EnableTransactionConnection){
            ConnectionDirtyDataQueue cache= ((EnableTransactionConnection) connection).getDirtyData();
            cache.add(primaryKeyValue, INSERT,data,row,sqlUtils);
        }
    }
    public static void putUpdateDirty(Connection connection,String primaryKeyValue,Object data,long row,SQLUtils sqlUtils){
        if(connection instanceof EnableTransactionConnection){
            ConnectionDirtyDataQueue cache= ((EnableTransactionConnection) connection).getDirtyData();
            cache.add(primaryKeyValue, UPDATE,data,row,sqlUtils);
        }
    }
    public static void putDeleteDirty(Connection connection,String primaryKeyValue,Object data,long row,SQLUtils sqlUtils){
        if(connection instanceof EnableTransactionConnection){
            ConnectionDirtyDataQueue cache= ((EnableTransactionConnection) connection).getDirtyData();
            cache.add(primaryKeyValue, DELETE,data,row,sqlUtils);
        }
    }
}
