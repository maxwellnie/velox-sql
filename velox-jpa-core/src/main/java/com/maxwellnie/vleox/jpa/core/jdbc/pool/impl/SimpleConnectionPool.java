package com.maxwellnie.vleox.jpa.core.jdbc.pool.impl;

import com.maxwellnie.vleox.jpa.core.exception.ConnectionPoolBusyException;
import com.maxwellnie.vleox.jpa.core.jdbc.connection.AutoCallBackConnection;
import com.maxwellnie.vleox.jpa.core.jdbc.pool.ConnectionPool;
import com.maxwellnie.vleox.jpa.core.utils.jdbc.ConnectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 连接池
 *
 * @author Maxwell Nie
 */
public class SimpleConnectionPool extends ConnectionPool {
    private static final Object lock = new Object();
    private static Logger logger = LoggerFactory.getLogger(SimpleConnectionPool.class);
    protected String driverClassName;
    protected String username;
    protected String url;
    protected String password;
    protected ConnectionUtils connectionUtils;
    private volatile boolean isInit = false;

    public SimpleConnectionPool() {
        super(20);
    }

    public SimpleConnectionPool(int size) {
        super(size);
    }

    private synchronized void init() {
        logger.debug("The connection pool starts to be initialized. The configuration information is as follows:" + getProperties() + "maximum:" + maximum);
        this.connectionUtils = new ConnectionUtils(this, driverClassName, username, url, password);
        try {
            for (int i = 0; i < maximum; i++)
                pool.add(connectionUtils.establishConnection());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        isInit = true;
        logger.debug("pool size:" + pool.size());
    }

    @Override
    public Connection getConnection() throws ConnectionPoolBusyException {
        Connection connection = null;
        synchronized (lock) {
            if (!isInit) {
                init();
            }
            while (pool.size() == 0) {
                try {
                    lock.wait(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            connection = pool.remove(Math.max(pool.size() - 1, 0));
        }

        logger.debug("A connection was obtained from the connection pool:" + connection);
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new SQLException("not support method!");
    }

    @Override
    public void callBack(Connection connection) {
        synchronized (lock) {
            if (!isPoolFill(1)) {
                logger.debug("Returned a connection to the connection pool:" + connection);
                if (connection instanceof AutoCallBackConnection)
                    pool.add(connection);
                else
                    pool.add(new AutoCallBackConnection(connection, this));
            }
            lock.notifyAll();
        }
        logger.debug("pool size:" + pool.size());
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException("not support method!");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new SQLException("not support method!");
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ConnectionUtils getConnectionUtils() {
        return connectionUtils;
    }

    public void setConnectionUtils(ConnectionUtils connectionUtils) {
        this.connectionUtils = connectionUtils;
    }

    public String getProperties() {
        return "{" +
                "driverClassName='" + driverClassName + '\'' +
                ", username='" + username + '\'' +
                ", url='" + url + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}
