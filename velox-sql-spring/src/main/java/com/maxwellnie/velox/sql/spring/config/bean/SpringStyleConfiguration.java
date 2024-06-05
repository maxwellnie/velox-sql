package com.maxwellnie.velox.sql.spring.config.bean;

import com.maxwellnie.velox.sql.core.cache.impl.LRUCache;
import com.maxwellnie.velox.sql.core.natives.dao.BaseDao;
import com.maxwellnie.velox.sql.core.natives.task.DefaultTaskQueue;

import java.sql.Connection;

/**
 * @author Maxwell Nie
 */
public class SpringStyleConfiguration {
    /**
     * daoImplClassName
     */
    private String daoImplClassName = BaseDao.class.getName();
    /**
     * tablePrefix
     */
    private String tablePrefix = "";
    /**
     * standColumn
     */
    private boolean standColumn = false;
    private boolean standTable = false;
    private boolean isCache;
    private boolean isTaskQueue;
    /**
     * level
     */
    private int level = Connection.TRANSACTION_REPEATABLE_READ;
    /**
     * cacheClassName
     */
    private String cacheClassName = LRUCache.class.getName();
    private String taskQueueClassName = DefaultTaskQueue.class.getName();
    private String dialect;

    public boolean getIsTaskQueue() {
        return isTaskQueue;
    }

    public void setIsTaskQueue(boolean taskQueue) {
        isTaskQueue = taskQueue;
    }

    public String getTaskQueueClassName() {
        return taskQueueClassName;
    }

    public void setTaskQueueClassName(String taskQueueClassName) {
        this.taskQueueClassName = taskQueueClassName;
    }

    public String getDaoImplClassName() {
        return daoImplClassName;
    }

    public void setDaoImplClassName(String daoImplClassName) {
        this.daoImplClassName = daoImplClassName;
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

    public boolean isStandTable() {
        return standTable;
    }

    public void setStandTable(boolean standTable) {
        this.standTable = standTable;
    }

    public boolean isCache() {
        return isCache;
    }

    public void setCache(boolean cache) {
        isCache = cache;
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

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }
}
