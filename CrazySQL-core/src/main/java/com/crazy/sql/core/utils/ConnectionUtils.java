package com.crazy.sql.core.utils;

import com.crazy.sql.core.pool.ConnectionPool;
import com.crazy.sql.core.jdbc.AutoCallBackConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 数据库连接工具
 */
public class ConnectionUtils {
    private Properties properties=null;
    private static Logger logger= LoggerFactory.getLogger(ConnectionUtils.class);
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
        AutoCallBackConnection connection=new AutoCallBackConnection(DriverManager.getConnection(properties.getProperty("url"),properties.getProperty("username"),properties.getProperty("password")),pool);
        logger.info("connection init:"+connection);
        return connection;
    }
}
