package com.maxwellnie.vleox.jpa.core.jdbc.pool;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 连接池
 */
public abstract class ConnectionPool implements DataSource {
    protected List<Connection> pool = Collections.synchronizedList(new LinkedList<>());
    protected volatile int maximum;

    public ConnectionPool(int maximum) {
        this.maximum = maximum;
    }

    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }

    /**
     * 回收数据库连接
     *
     * @param connection
     */
    public abstract void callBack(Connection connection);

    public abstract Connection getConnection();

    /**
     * 数据库连接池是否已满
     *
     * @param count
     * @return
     */
    public boolean isPoolFill(int count) {
        return pool.size() + count > maximum;
    }

    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        DriverManager.setLogWriter(out);
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getGlobal().getParent();
    }

    public int size() {
        return pool.size();
    }

    public int getMaximum() {
        return maximum;
    }
}
