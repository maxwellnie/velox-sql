package com.maxwellnie.velox.jpa.core.jdbc.pool;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 连接池
 *
 * @deprecated 我们提供的连接池是很基本的连接池，我们并不建议使用这个连接池，可能会带来一些问题，将会在2.x版本彻底弃用。
 */
@Deprecated
public abstract class ConnectionPool implements DataSource {
    protected List<Connection> pool = Collections.synchronizedList(new LinkedList<>());
    protected volatile int maximum;

    public ConnectionPool(int maximum) {
        this.maximum = maximum;
    }

    public int getLoginTimeout() {
        return DriverManager.getLoginTimeout();
    }

    public void setLoginTimeout(int seconds) {
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

    public PrintWriter getLogWriter() {
        return DriverManager.getLogWriter();
    }

    public void setLogWriter(PrintWriter out) {
        DriverManager.setLogWriter(out);
    }

    public Logger getParentLogger() {
        return Logger.getGlobal().getParent();
    }

    public int size() {
        return pool.size();
    }

    public int getMaximum() {
        return maximum;
    }
}
