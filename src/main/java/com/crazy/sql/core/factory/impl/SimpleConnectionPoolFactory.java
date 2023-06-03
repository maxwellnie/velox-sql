package com.crazy.sql.core.factory.impl;

import com.crazy.sql.core.config.SQLConfig;
import com.crazy.sql.core.factory.ConnectionPoolFactory;
import com.crazy.sql.core.pool.ConnectionPool;
import com.crazy.sql.core.pool.impl.SimpleConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * 标准的连接池工厂
 */
public class SimpleConnectionPoolFactory extends ConnectionPoolFactory {
    private SQLConfig sqlConfig=SQLConfig.getInstance();
    private static final int maximumConnection=10;

    @Override
    public ConnectionPool produce() {
        return produce(sqlConfig.getDiverClassName(),sqlConfig.getUrl(),sqlConfig.getUserName(),sqlConfig.getPassword(),sqlConfig.getMaximum());
    }

    @Override
    public ConnectionPool produce(Properties properties) {
        return produce(properties,maximumConnection);
    }

    @Override
    public ConnectionPool produce(Map<String, String> properties) {
        return produce(properties,maximumConnection);
    }

    @Override
    public ConnectionPool produce(String driverClassName, String url, String userName, String password) {
        Properties properties=new Properties();
        properties.put("driverClassName",driverClassName);
        properties.put("url",url);
        properties.put("userName",userName);
        properties.put("password",password);
        return produce(properties,maximumConnection);
    }

    @Override
    public ConnectionPool produce(Map<String, String> properties, int maximumConnection) {
        Properties prop=new Properties();
        prop.putAll(properties);
        return produce(prop,maximumConnection);
    }

    @Override
    public ConnectionPool produce(Properties properties, int maximumConnection) {
        return new SimpleConnectionPool(properties,maximumConnection);
    }

    @Override
    public ConnectionPool produce(String driverClassName, String url, String userName, String password, int maximumConnection) {
        Properties properties=new Properties();
        properties.put("driverClassName",driverClassName);
        properties.put("url",url);
        properties.put("userName",userName);
        properties.put("password",password);
        return produce(properties,maximumConnection);
    }
}
