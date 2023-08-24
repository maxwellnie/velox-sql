package com.crazy.sql.core.jdbc.dirtydata.queue;

import com.crazy.sql.core.jdbc.dirtydata.DirtyData;
import com.crazy.sql.core.utils.SQLUtils;

import java.util.*;

public class ConnectionDirtyDataQueue {
    private final List<DirtyData> dirtyData= Collections.synchronizedList(new LinkedList<>());
    public static final int INSERT=0;
    public static final int UPDATE=1;
    public static final int DELETE=2;
    public void add(String key,int mode,Object data,long row,SQLUtils sqlUtils) {
        dirtyData.add(new DirtyData(key,mode,data,row,sqlUtils));
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
