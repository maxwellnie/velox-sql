package com.maxwellnie.velox.jpa.core.utils.jdbc;

import com.maxwellnie.velox.jpa.core.jdbc.pool.ConnectionPool;
import com.maxwellnie.velox.jpa.core.jdbc.connection.CallBackConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 数据库连接工具
 *
 * @author Maxwell Nie
 */
public class ConnectionUtils {
    private static Logger logger = LoggerFactory.getLogger(ConnectionUtils.class);
    protected String username;
    protected String url;
    protected String password;
    private ConnectionPool pool;
    private String diverClassName;

    public ConnectionUtils(ConnectionPool pool, String diverClassName, String username, String url, String password) {
        this.diverClassName = diverClassName;
        this.pool = pool;
        this.username = username;
        this.url = url;
        this.password = password;
    }


    /**
     * 建立与数据库的连接
     *
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection establishConnection() throws SQLException, ClassNotFoundException {
        Class.forName(diverClassName);
        CallBackConnection connection = new CallBackConnection(DriverManager.getConnection(url, username, password), pool);
        logger.debug("connection init:" + connection);
        return connection;
    }
}
