package com.crazy.sql.core.builder;

import com.crazy.sql.core.CrazySQL;
import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.factory.ConnectionPoolFactory;
import com.crazy.sql.core.pool.ConnectionPool;
import com.crazy.sql.core.utils.SQLUtils;

/**
 * 建造者模式，用于创建CrazySQL实例
 * @param <T>
 */
public interface AbstractCrazySQLBuilder<T>{
    /**
     * 设置连接池
     * @param pool
     * @return
     */
    AbstractCrazySQLBuilder<T> setPool(ConnectionPool pool);

    /**
     * 设置开启事务
     * @return
     */
    AbstractCrazySQLBuilder<T> transaction(boolean b);

    /**
     * 设置SQLUtils
     * @param sqlUtils
     * @return
     */
    AbstractCrazySQLBuilder<T> SQLUtils(SQLUtils<T> sqlUtils);

    /**
     * 开启缓存，默认为内存缓存
     * @return
     */
    AbstractCrazySQLBuilder<T> enableCache(CacheManager cacheManager);

    /**
     * 构建CrazySQL对象
     * @return
     */
    CrazySQL<T> build();
}
