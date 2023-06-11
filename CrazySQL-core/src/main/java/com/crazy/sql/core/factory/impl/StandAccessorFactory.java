package com.crazy.sql.core.factory.impl;

import com.crazy.sql.core.Accessor;
import com.crazy.sql.core.builder.impl.StandAccessorBuilder;
import com.crazy.sql.core.config.CrazySQLConfig;
import com.crazy.sql.core.builder.AbstractAccessorBuilder;
import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.factory.AccessorFactory;
import com.crazy.sql.core.utils.SQLUtils;

import javax.sql.DataSource;

public class StandAccessorFactory extends AccessorFactory {


    public StandAccessorFactory(DataSource pool, CacheManager cacheManager, boolean enableTransaction) {
        super(pool, cacheManager, enableTransaction);
    }

    public StandAccessorFactory(DataSource pool, boolean enableTransaction) {
        super(pool, enableTransaction);
    }

    @Override
    public <T> Accessor<T> produce(Class<T> tClass) {
        CrazySQLConfig crazySqlConfig = CrazySQLConfig.getInstance();
        AbstractAccessorBuilder<T> crazySQLBuilder=new StandAccessorBuilder<>();
        crazySQLBuilder=crazySQLBuilder.setPool(getConnectionPool())
                .transaction(isEnableTransaction())
                .SQLUtils(new SQLUtils<>(tClass, crazySqlConfig.getTableSuffix(), crazySqlConfig.isStandColumn()));
        if(getCacheManager()!=null)
            crazySQLBuilder.enableCache(getCacheManager());
        return crazySQLBuilder.build();
    }
}
