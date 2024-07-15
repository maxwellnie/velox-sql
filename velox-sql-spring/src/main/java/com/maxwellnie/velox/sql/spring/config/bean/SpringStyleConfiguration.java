package com.maxwellnie.velox.sql.spring.config.bean;

import com.maxwellnie.velox.sql.core.cache.impl.LRUCache;
import com.maxwellnie.velox.sql.core.natives.dao.BaseDao;
import com.maxwellnie.velox.sql.core.natives.jdbc.dialect.Dialect;
import com.maxwellnie.velox.sql.core.natives.jdbc.dialect.MySqlDialect;
import com.maxwellnie.velox.sql.core.natives.task.DefaultTaskQueue;
import com.maxwellnie.velox.sql.core.natives.task.TaskQueue;
import com.maxwellnie.velox.sql.spring.support.NoSpringTransactionTask;

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
    private Dialect dialect = new MySqlDialect();
    private String transactionTaskClassName = NoSpringTransactionTask.class.getName();
    private TaskQueue taskQueue = new DefaultTaskQueue();

    public TaskQueue getTaskQueue() {
        return taskQueue;
    }

    public void setTaskQueue(TaskQueue taskQueue) {
        this.taskQueue = taskQueue;
    }

    public boolean getIsTaskQueue() {
        return isTaskQueue;
    }

    public void setIsTaskQueue(boolean taskQueue) {
        isTaskQueue = taskQueue;
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

    public Dialect getDialect() {
        return dialect;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    public String getTransactionTaskClassName() {
        return transactionTaskClassName;
    }

    public void setTransactionTaskClassName(String transactionTaskClassName) {
        this.transactionTaskClassName = transactionTaskClassName;
    }
}
