package com.maxwellnie.vleox.jpa.core.config;

import com.maxwellnie.vleox.jpa.core.cahce.Cache;
import com.maxwellnie.vleox.jpa.core.cahce.impl.LRUCache;
import com.maxwellnie.vleox.jpa.core.dao.support.DaoImpl;

import java.sql.Connection;

/**
 * @author Maxwell Nie
 */
public abstract class BaseConfig {
    private String tablePrefix = "";
    private boolean standColumn = false;
    private boolean standTable = false;
    private boolean isCache;
    private Class<?>[] clazzArr;
    private Class<?> daoImplClazz = DaoImpl.class;
    private int level = Connection.TRANSACTION_REPEATABLE_READ;
    private Class<? extends Cache> cacheClass = LRUCache.class;


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

    public Class<?> getDaoImplClazz() {
        return daoImplClazz;
    }

    public void setDaoImplClazz(Class<?> daoImplClazz) {
        this.daoImplClazz = daoImplClazz;
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
