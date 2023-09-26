package com.crazy.sql.core.config;


import com.crazy.sql.core.accessor.Accessor;
import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.cahce.impl.SimpleCache;

import java.sql.Connection;

/**
 * 单例模式，饿汉式，设置全局唯一配置
 * @author A
 */
public class GlobalConfig {
    private String tablePrefix ="";
    private boolean standColumn=false;
    private boolean standTable=false;
    private boolean isCache;
    private Class<?>[] clazzArr;
    private Class<?> accessorClazz= Accessor.class;
    private int level= Connection.TRANSACTION_REPEATABLE_READ;
    private Class<? extends Cache> cacheClass= SimpleCache.class;
    private static final GlobalConfig INSTANCE =new GlobalConfig();
    private GlobalConfig(){

    }
    public static GlobalConfig getInstance(){
        return INSTANCE;
    }

    public boolean isStandTable() {
        return standTable;
    }

    public void setStandTable(boolean standTable) {
        this.standTable = standTable;
    }
    public String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public boolean isStandColumn() {
        return standColumn;
    }

    public void setStandColumn(boolean standColumn) {
        this.standColumn = standColumn;
    }

    public Class<?>[] getClazzArr() {
        return clazzArr;
    }

    public void setClazzArr(Class<?>[] clazzArr) {
        this.clazzArr = clazzArr;
    }

    public boolean isCache() {
        return isCache;
    }

    public void setCache(boolean cache) {
        isCache = cache;
    }

    public Class<?> getAccessorClazz() {
        return accessorClazz;
    }

    public void setAccessorClazz(Class<?> accessorClazz) {
        this.accessorClazz = accessorClazz;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Class<? extends Cache> getCacheClass() {
        return cacheClass;
    }

    public void setCacheClass(Class<? extends Cache> cacheClass) {
        this.cacheClass = cacheClass;
    }
}
