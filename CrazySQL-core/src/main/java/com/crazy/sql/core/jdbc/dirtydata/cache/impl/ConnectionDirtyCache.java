package com.crazy.sql.core.jdbc.dirtydata.cache.impl;

import com.crazy.sql.core.jdbc.dirtydata.DirtyData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionDirtyCache{
    private final List<DirtyData> dirtyData= Collections.synchronizedList(new LinkedList<>());
    public static final int INSERT=0;
    public static final int UPDATE=1;
    public static final int DELETE=2;
    public void add(int key,int mode,Object data) {
        dirtyData.add(new DirtyData(key,mode,data));
    }
    public int size() {
        return dirtyData.size();
    }
    public List<DirtyData> getDirtyData(){
        return this.dirtyData;
    }
    public void clear() {
        dirtyData.clear();
    }
}
