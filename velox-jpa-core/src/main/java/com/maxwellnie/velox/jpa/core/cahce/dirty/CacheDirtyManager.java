package com.maxwellnie.velox.jpa.core.cahce.dirty;

import com.maxwellnie.velox.jpa.core.cahce.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Maxwell Nie
 */
public class CacheDirtyManager {
    private static final Logger logger = LoggerFactory.getLogger(CacheDirtyManager.class);
    private Map<Cache, DirtyDataManager> dirtyManagerMap = Collections.synchronizedMap(new LinkedHashMap<>());

    public void put(Cache cache, DirtyDataManager dirtyDataManager) {
        dirtyManagerMap.put(cache, dirtyDataManager);
    }

    public DirtyDataManager get(Cache cache) {
        DirtyDataManager dirtyDataManager = dirtyManagerMap.get(cache);
        if (dirtyDataManager == null) {
            dirtyDataManager = new SimpleDirtyDataManager(cache);
            dirtyManagerMap.put(cache, dirtyDataManager);
        }
        return dirtyDataManager;
    }

    public void commit() {
        for (DirtyDataManager dirtyDataManager : dirtyManagerMap.values())
            dirtyDataManager.commit();
    }

    public void rollback() {
        for (DirtyDataManager dirtyDataManager : dirtyManagerMap.values())
            dirtyDataManager.rollback();
    }

    public void clear() {
        logger.debug("The dirty data is cleared.");
        dirtyManagerMap.clear();
    }

}
