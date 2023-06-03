package com.crazy.sql.core.builder.impl;

import com.crazy.sql.core.CrazySQL;
import com.crazy.sql.core.builder.AbstractCrazySQLBuilder;
import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.config.SQLConfig;
import com.crazy.sql.core.executor.impl.NotCallBackSimpleSQLExecutor;
import com.crazy.sql.core.executor.impl.StandSimpleSQLExecutor;
import com.crazy.sql.core.factory.ConnectionPoolFactory;
import com.crazy.sql.core.factory.impl.SimpleConnectionPoolFactory;
import com.crazy.sql.core.pool.ConnectionPool;
import com.crazy.sql.core.proxy.SimpleSQLExecutorProxy;
import com.crazy.sql.core.utils.SQLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CrazySQL的标准构建者
 * @param <T>
 */
public class StandCrazySQLBuilder<T> implements AbstractCrazySQLBuilder<T> {
    private static Logger logger= LoggerFactory.getLogger(StandCrazySQLBuilder.class);
    private final CrazySQL<T> crazySQL;
    public StandCrazySQLBuilder() {
        logger.info(SQLConfig.getInstance().toString());
        crazySQL=new CrazySQL<>();
        crazySQL.setExecutor(new StandSimpleSQLExecutor<>());
    }

    @Override
    public AbstractCrazySQLBuilder<T> setPool(ConnectionPool pool) {
        crazySQL.setPool(pool);
        return this;
    }

    @Override
    public AbstractCrazySQLBuilder<T> transaction(boolean b) {
        if(b)
            crazySQL.setExecutor(new SimpleSQLExecutorProxy<>(new NotCallBackSimpleSQLExecutor<>()));
        return this;
    }

    @Override
    public AbstractCrazySQLBuilder<T> SQLUtils(SQLUtils<T> sqlUtils) {
        crazySQL.setSqlUtils(sqlUtils);
        return this;
    }

    @Override
    public AbstractCrazySQLBuilder<T> enableCache(CacheManager cacheManager) {
        crazySQL.setCacheManager(cacheManager);
        return this;
    }

    @Override
    public CrazySQL<T> build() {
        return crazySQL;
    }
}
