package com.crazy.sql.core.builder.impl;

import com.crazy.sql.core.Accessor;
import com.crazy.sql.core.executor.impl.NotCallBackSimpleSQLExecutor;
import com.crazy.sql.core.executor.impl.StandSimpleSQLExecutor;
import com.crazy.sql.core.proxy.SimpleSQLExecutorProxy;
import com.crazy.sql.core.builder.AbstractAccessorBuilder;
import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.config.CrazySQLConfig;
import com.crazy.sql.core.utils.SQLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * CrazySQL的标准构建者
 * @param <T>
 */
public class StandAccessorBuilder<T> implements AbstractAccessorBuilder<T> {
    private static Logger logger= LoggerFactory.getLogger(StandAccessorBuilder.class);
    private final Accessor<T> accessor;
    public StandAccessorBuilder() {
        logger.info(CrazySQLConfig.getInstance().toString());
        accessor =new Accessor<>();
        accessor.setExecutor(new StandSimpleSQLExecutor<>());
    }

    @Override
    public AbstractAccessorBuilder<T> setPool(DataSource pool) {
        accessor.setPool(pool);
        return this;
    }

    @Override
    public AbstractAccessorBuilder<T> springTransaction(boolean b) {
        if(b)
            accessor.setExecutor(new SimpleSQLExecutorProxy<>(new NotCallBackSimpleSQLExecutor<>()));
        return this;
    }

    @Override
    public AbstractAccessorBuilder<T> SQLUtils(SQLUtils<T> sqlUtils) {
        accessor.setSqlUtils(sqlUtils);
        return this;
    }

    @Override
    public AbstractAccessorBuilder<T> enableCache(CacheManager cacheManager) {
        accessor.setCacheManager(cacheManager);
        return this;
    }

    @Override
    public Accessor<T> build() {
        return accessor;
    }
}
