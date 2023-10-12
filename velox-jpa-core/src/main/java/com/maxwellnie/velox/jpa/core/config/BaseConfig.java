package com.maxwellnie.velox.jpa.core.config;

import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.cahce.impl.LRUCache;

import java.sql.Connection;

/**
 * @author Maxwell Nie
 */
public abstract class BaseConfig {
    private String tablePrefix = "";
    private boolean standColumn = false;
    private boolean standTable = false;
    private boolean isCache;
    private static String daoImplClassName ="com.maxwellnie.velox.jpa.core.template.dao.TemplateDao";
    private int level = Connection.TRANSACTION_REPEATABLE_READ;
    private String cacheClassName = LRUCache.class.getName();


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


    public boolean isCache() {
        return isCache;
    }

    public void setCache(boolean cache) {
        isCache = cache;
    }

    public static String getDaoImplClassName() {
        return BaseConfig.daoImplClassName;
    }

    public static void setDaoImplClassName(String daoImplClassName) {
        BaseConfig.daoImplClassName = daoImplClassName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getCacheClassName() {
        return cacheClassName;
    }

    public void setCacheClassName(String cacheClassName) {
        this.cacheClassName = cacheClassName;
    }
}
