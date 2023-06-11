package com.crazy.sql.core.factory;

import com.crazy.sql.core.Accessor;
import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.pool.ConnectionPool;
import com.crazy.sql.core.utils.SQLUtils;

import javax.sql.DataSource;

public abstract class AccessorFactory {
    private DataSource pool ;
    private CacheManager cacheManager;
    private boolean enableTransaction;

    public AccessorFactory(DataSource pool, CacheManager cacheManager, boolean enableTransaction) {
        this.pool = pool;
        this.cacheManager = cacheManager;
        this.enableTransaction = enableTransaction;
    }
    public AccessorFactory(DataSource pool, boolean enableTransaction) {
        this.pool = pool;
        this.enableTransaction = enableTransaction;
    }
    public abstract <T> Accessor<T> produce(Class<T> tClasss);
    public DataSource getConnectionPool() {
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
