package com.crazy.sql.core.utils;

import com.crazy.sql.core.pool.ConnectionPool;
import com.crazy.sql.core.proxy.AutoCallBackConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库连接工具
 */
public class ConnectionUtils {
    private Properties properties=null;
    private ConnectionPool pool;

    public ConnectionUtils(Properties properties,ConnectionPool pool) {
        this.properties = properties;
        this.pool=pool;
    }

    /**
     * 建立与数据库的连接
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection establishConnection()throws SQLException {
        return new AutoCallBackConnection(DriverManager.getConnection(properties.getProperty("url"),properties.getProperty("userName"),properties.getProperty("password")),pool);
    }
}
