package com.crazy.sql.core.cahce.dirty;

import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.cahce.key.CacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Akiba no ichiichiyoha
 */
public class SimpleDirtyDataManager implements DirtyDataManager{
   private final List<DirtyData> dirtyDataList = Collections.synchronizedList(new LinkedList<>());
   private static final Logger logger= LoggerFactory.getLogger(SimpleDirtyDataManager.class);
   private Cache cache;
   private boolean clear;

    public SimpleDirtyDataManager(Cache cache) {
        this.cache = cache;
    }

    public void put(CacheKey key, Object data){
       dirtyDataList.add(new DirtyData(key,data));
   }
   @Override
   public void commit(){
       if(clear){
           cache.clear();
       }
       clear=false;
       for (DirtyData dirtyData: dirtyDataList) {
           cache.put(dirtyData.getKey(),dirtyData.getData());
       }
       dirtyDataList.clear();
   }
   @Override
   public void rollback(){
       clear=false;
       dirtyDataList.clear();
   }
   @Override
   public void clear(){
       clear=true;
       dirtyDataList.clear();
   }
}
