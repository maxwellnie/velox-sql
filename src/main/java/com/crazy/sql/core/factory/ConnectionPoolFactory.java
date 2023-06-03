package com.crazy.sql.core.factory;

import com.crazy.sql.core.pool.ConnectionPool;

import java.util.Map;
import java.util.Properties;

/**
 * 工厂方法模式，创建连接池
 */
public abstract class ConnectionPoolFactory {
    public abstract ConnectionPool produce();
    public abstract ConnectionPool produce(Properties properties);
    public abstract ConnectionPool produce(Map<String,String> properties);
    public abstract ConnectionPool produce(String driverClassName, String url, String userName, String password);
    public abstract ConnectionPool produce(Map<String,String> properties, int maximumConnection);
    public abstract ConnectionPool produce(Properties properties, int maximumConnection);
    public abstract ConnectionPool produce(String driverClassName, String url, String userName, String password, int maximumConnection);
}
