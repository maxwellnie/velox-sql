package com.crazy.sql.core.factory.impl;

import com.crazy.sql.core.CrazySQL;
import com.crazy.sql.core.builder.AbstractCrazySQLBuilder;
import com.crazy.sql.core.builder.impl.StandCrazySQLBuilder;
import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.executor.SimpleSQLExecutor;
import com.crazy.sql.core.executor.impl.StandSimpleSQLExecutor;
import com.crazy.sql.core.factory.ConnectionPoolFactory;
import com.crazy.sql.core.factory.CrazySQLFactory;
import com.crazy.sql.core.pool.ConnectionPool;
import com.crazy.sql.core.utils.SQLUtils;

public class StandCrazySQLFactory extends CrazySQLFactory {
    public StandCrazySQLFactory(ConnectionPoolFactory factory,boolean enableTransaction) {
        super(factory,enableTransaction);
    }

    public StandCrazySQLFactory(ConnectionPoolFactory factory, CacheManager cacheManager, boolean enableTransaction) {
        super(factory, cacheManager, enableTransaction);
    }

    @Override
    public <T> CrazySQL<T> produce(SQLUtils<T> sqlUtils) {
        AbstractCrazySQLBuilder<T> crazySQLBuilder=new StandCrazySQLBuilder<>();
        crazySQLBuilder=crazySQLBuilder.setPool(getConnectionPool())
                .transaction(isEnableTransaction())
                .SQLUtils(sqlUtils);
        if(getCacheManager()!=null)
            crazySQLBuilder.enableCache(getCacheManager());
        return crazySQLBuilder.build();
    }
}
