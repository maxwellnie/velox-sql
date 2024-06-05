package com.maxwellnie.velox.sql.core.config;

import com.maxwellnie.velox.sql.core.cache.Cache;
import com.maxwellnie.velox.sql.core.cache.impl.LRUCache;
import com.maxwellnie.velox.sql.core.natives.jdbc.dialect.Dialect;
import com.maxwellnie.velox.sql.core.natives.jdbc.dialect.MySqlDialect;
import com.maxwellnie.velox.sql.core.natives.task.DefaultTaskQueue;
import com.maxwellnie.velox.sql.core.natives.task.TaskQueue;

import java.sql.Connection;

/**
 * Configuration
 * @author Maxwell Nie
 */
public abstract class Configuration {
    /**
     * daoImplClassName
     */
    private Class<?> daoImplClass;
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
    /**
     * level
     */
    private int level = Connection.TRANSACTION_REPEATABLE_READ;
    private Dialect dialect = new MySqlDialect();
    private boolean isTaskQueue;
    private Class<? extends TaskQueue> taskQueueClass = DefaultTaskQueue.class;
    /**
     * cacheClassName
     */
    private Class<? extends Cache> cacheClass = LRUCache.class;

    public boolean getIsTaskQueue() {
        return isTaskQueue;
    }

    public void setIsTaskQueue(boolean taskQueue) {
        isTaskQueue = taskQueue;
    }

    public Class<? extends TaskQueue> getTaskQueueClass() {
        return taskQueueClass;
    }

    public void setTaskQueueClass(Class<? extends TaskQueue> taskQueueClass) {
        this.taskQueueClass = taskQueueClass;
    }

    public Class<?> getDaoImplClass() {
        return daoImplClass;
    }

    public  void setDaoImplClass(Class<?> daoImplClass) {
        this.daoImplClass = daoImplClass;
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

    public Class<? extends Cache> getCacheClass() {
        return cacheClass;
    }

    public void setCacheClass(Class<? extends Cache> cacheClass) {
        this.cacheClass = cacheClass;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }
}
