package com.crazy.sql.core.factory;

import com.crazy.sql.core.CrazySQL;
import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.executor.SimpleSQLExecutor;
import com.crazy.sql.core.pool.ConnectionPool;
import com.crazy.sql.core.utils.SQLUtils;

import java.sql.Connection;

public abstract class CrazySQLFactory {
    private ConnectionPool pool ;
    private CacheManager cacheManager;
    private boolean enableTransaction;

    public CrazySQLFactory(ConnectionPoolFactory factory, CacheManager cacheManager, boolean enableTransaction) {
        this.pool = factory.produce();
        this.cacheManager = cacheManager;
        this.enableTransaction = enableTransaction;
    }
    public CrazySQLFactory(ConnectionPoolFactory factory, boolean enableTransaction) {
        this.pool = factory.produce();
        this.enableTransaction = enableTransaction;
    }
    public abstract <T> CrazySQL<T> produce(SQLUtils<T> sqlUtils);
    public ConnectionPool getConnectionPool() {
        return pool;
    }

    public void setConnectionPool(ConnectionPool pool) {
        this.pool = pool;
    }

    public boolean isEnableTransaction() {
        return enableTransaction;
    }

    public void setEnableTransaction(boolean enableTransaction) {
        this.enableTransaction = enableTransaction;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}
