package com.crazy.sql.core.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库连接工具
 */
public class ConnectionUtils {
    private Properties properties=null;

    public ConnectionUtils(Properties properties) {
        this.properties = properties;
    }

    /**
     * 建立与数据库的连接
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection establishConnection()throws SQLException {
        return DriverManager.getConnection(properties.getProperty("url"),properties.getProperty("userName"),properties.getProperty("password"));
    }
}
