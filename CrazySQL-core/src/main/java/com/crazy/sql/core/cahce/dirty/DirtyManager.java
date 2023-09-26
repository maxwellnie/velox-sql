package com.crazy.sql.core.cahce.dirty;

import com.crazy.sql.core.cahce.Cache;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Akiba no ichiichiyoha
 */
public class DirtyManager {
    private Map<Cache,DirtyDataManager> dirtyManagerMap= Collections.synchronizedMap(new LinkedHashMap<>());
    public void put(Cache cache,DirtyDataManager dirtyDataManager){
        dirtyManagerMap.put(cache,dirtyDataManager);
    }
    public DirtyDataManager get(Cache cache){
        DirtyDataManager dirtyDataManager=dirtyManagerMap.get(cache);
        if(dirtyDataManager==null){
            dirtyDataManager=new SimpleDirtyDataManager(cache);
            dirtyManagerMap.put(cache,dirtyDataManager);
        }
        return dirtyDataManager;
    }
    public void commit(){
        for(DirtyDataManager dirtyDataManager:dirtyManagerMap.values())
            dirtyDataManager.commit();
    }
    public void rollback(){
        for(DirtyDataManager dirtyDataManager:dirtyManagerMap.values())
            dirtyDataManager.rollback();
    }
    public void clear(){
        dirtyManagerMap.clear();
    }
}
