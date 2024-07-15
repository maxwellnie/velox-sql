package com.maxwellnie.velox.sql.core.config;

import com.maxwellnie.velox.sql.core.cache.Cache;
import com.maxwellnie.velox.sql.core.cache.impl.LRUCache;
import com.maxwellnie.velox.sql.core.distributed.NoTransactionTask;
import com.maxwellnie.velox.sql.core.distributed.TransactionTask;
import com.maxwellnie.velox.sql.core.natives.jdbc.dialect.Dialect;
import com.maxwellnie.velox.sql.core.natives.task.DefaultTaskQueue;
import com.maxwellnie.velox.sql.core.natives.task.TaskQueue;
import com.maxwellnie.velox.sql.core.natives.type.convertor.impl.json.JsonSupporter;

import java.sql.Connection;

/**
 * Configuration
 *
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
    private Dialect dialect = null;
    private Class<? extends TransactionTask> transactionTaskClass = NoTransactionTask.class;
    private boolean isTaskQueue;
    private TaskQueue taskQueue = new DefaultTaskQueue();
    private JsonSupporter jsonSupporter;
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


    public Class<?> getDaoImplClass() {
        return daoImplClass;
    }

    public void setDaoImplClass(Class<?> daoImplClass) {
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

    public JsonSupporter getJsonSupporter() {
        return jsonSupporter;
    }

    public Class<? extends TransactionTask> getTransactionTaskClass() {
        return transactionTaskClass;
    }

    public void setTransactionTaskClass(Class<? extends TransactionTask> transactionTaskClass) {
        this.transactionTaskClass = transactionTaskClass;
    }

    public TaskQueue getTaskQueue() {
        return taskQueue;
    }

    public void setTaskQueue(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    public void setJsonSupporter(JsonSupporter jsonSupporter) {
        this.jsonSupporter = jsonSupporter;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }
}
