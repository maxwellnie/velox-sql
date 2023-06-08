package com.crazy.sql.core.pool.impl;

import com.crazy.sql.core.exception.ConnectionPoolBusyException;
import com.crazy.sql.core.factory.impl.SimpleConnectionPoolFactory;
import com.crazy.sql.core.pool.ConnectionPool;
import com.crazy.sql.core.utils.ConnectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 标准的连接池
 */
public class SimpleConnectionPool extends ConnectionPool {
    private static Logger logger= LoggerFactory.getLogger(SimpleConnectionPool.class);
    protected  Properties properties;
    protected ConnectionUtils connectionUtils;

    public SimpleConnectionPool(Properties properties,int maximumConnection) {
        logger.info("The connection pool starts to be initialized. The configuration information is as follows:"+properties+"\nmaximum:"+maximumConnection);
        this.properties=properties;
        this.maximum=maximumConnection;
        this.connectionUtils=new ConnectionUtils(properties,this);
        try {
            Class.forName(properties.getProperty("driverClassName"));
            for (int i=0;i<maximum;i++)
                pool.add(connectionUtils.establishConnection());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public synchronized Connection getConnection() throws ConnectionPoolBusyException {
        int count=0;
        while (pool.size() == 0) {
            try {
                if(count>100)
                    throw new ConnectionPoolBusyException();
                Thread.sleep(10);
                count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Connection connection=pool.remove(pool.size()-1);
        logger.info("A connection was obtained from the connection pool:"+connection);
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    @Override
    public synchronized void callBack(Connection connection){
        if (!isPoolFill(1)){
            logger.info("Returned a connection to the connection pool:"+connection);
            pool.add(connection);
        }
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
