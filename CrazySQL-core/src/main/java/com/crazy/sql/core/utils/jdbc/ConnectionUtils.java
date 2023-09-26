package com.crazy.sql.core.utils.jdbc;

import com.crazy.sql.core.jdbc.pool.ConnectionPool;
import com.crazy.sql.core.jdbc.connection.AutoCallBackConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 数据库连接工具
 * @author Akiba no ichiichiyoha
 */
public class ConnectionUtils {
    private static Logger logger= LoggerFactory.getLogger(ConnectionUtils.class);
    private ConnectionPool pool;
    private String diverClassName;
    protected String username;
    protected String url;
    protected String password;

    public ConnectionUtils(ConnectionPool pool,String diverClassName, String username, String url, String password) {
        this.diverClassName=diverClassName;
        this.pool = pool;
        this.username = username;
        this.url = url;
        this.password = password;
    }


    /**
     * 建立与数据库的连接
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection establishConnection() throws SQLException, ClassNotFoundException {
        Class.forName(diverClassName);
        AutoCallBackConnection connection=new AutoCallBackConnection(DriverManager.getConnection(url,username,password),pool);
        logger.debug("connection init:"+connection);
        return connection;
    }
}
