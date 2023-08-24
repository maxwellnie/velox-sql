package com.crazy.sql.core.builder;

import com.crazy.sql.core.Accessor;
import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.pool.ConnectionPool;
import com.crazy.sql.core.utils.SQLUtils;

import javax.sql.DataSource;

/**
 * 建造者模式，用于创建CrazySQL实例
 * @param <T>
 */
public interface AbstractAccessorBuilder<T>{
    /**
     * 设置连接池
     * @param pool
     * @return
     */
    AbstractAccessorBuilder<T> setPool(DataSource pool);

    /**
     * 设置对接spring事务
     * @return
     */
    AbstractAccessorBuilder<T> springTransaction(boolean b);

    /**
     * 设置SQLUtils
     * @param sqlUtils
     * @return
     */
    AbstractAccessorBuilder<T> SQLUtils(SQLUtils<T> sqlUtils);

    /**
     * 开启缓存，默认为内存缓存
     * @return
     */
    AbstractAccessorBuilder<T> enableCache(CacheManager cacheManager);

    /**
     * 构建CrazySQL对象
     * @return
     */
    Accessor<T> build();
}
