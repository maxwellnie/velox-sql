package com.maxwellnie.velox.sql.core.natives.jdbc.transaction.impl.base;

import com.maxwellnie.velox.sql.core.utils.jdbc.CurrentThreadUtils;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * 代理当前线程的数据源
 *
 * @author Maxwell Nie
 */
public class ProxyCurrentDataSource implements DataSource {
    /**
     * 默认数据源
     */
    private final DataSource defaultDataSource;

    public ProxyCurrentDataSource(DataSource defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
    }

    /**
     * 为当前线程寻找合适的连接对象
     *
     * @return
     * @throws SQLException
     */
    @Override
    public Connection getConnection() throws SQLException {
        DataSource currentDataSource = CurrentThreadUtils.getDataSource();
        if (currentDataSource == null) {
            return defaultDataSource.getConnection();
        }
        return currentDataSource.getConnection();
    }

    /**
     * 为当前线程寻找合适的连接对象
     *
     * @param username
     * @param password
     * @return
     * @throws SQLException
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        DataSource currentDataSource = CurrentThreadUtils.getDataSource();
        if (currentDataSource == null) {
            return defaultDataSource.getConnection(username, password);
        }
        return currentDataSource.getConnection(username, password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        DataSource currentDataSource = CurrentThreadUtils.getDataSource();
        if (currentDataSource == null) {
            return defaultDataSource.unwrap(iface);
        }
        return currentDataSource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        DataSource currentDataSource = CurrentThreadUtils.getDataSource();
        if (currentDataSource == null) {
            return defaultDataSource.isWrapperFor(iface);
        }
        return currentDataSource.isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        DataSource currentDataSource = CurrentThreadUtils.getDataSource();
        if (currentDataSource == null) {
            return defaultDataSource.getLogWriter();
        }
        return currentDataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        DataSource currentDataSource = CurrentThreadUtils.getDataSource();
        if (currentDataSource == null) {
            defaultDataSource.setLogWriter(out);
            return;
        }
        currentDataSource.setLogWriter(out);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        DataSource currentDataSource = CurrentThreadUtils.getDataSource();
        if (currentDataSource == null) {
            return defaultDataSource.getLoginTimeout();
        }
        return currentDataSource.getLoginTimeout();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        DataSource currentDataSource = CurrentThreadUtils.getDataSource();
        if (currentDataSource == null) {
            defaultDataSource.setLoginTimeout(seconds);
            return;
        }
        currentDataSource.setLoginTimeout(seconds);
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        DataSource currentDataSource = CurrentThreadUtils.getDataSource();
        if (currentDataSource == null) {
            return defaultDataSource.getParentLogger();
        }
        return currentDataSource.getParentLogger();
    }

    public DataSource getDefaultTarget() {
        return defaultDataSource;
    }

    /**
     * 获取当前线程的数据源
     *
     * @return
     */
    public DataSource getCurrentDataSource() {
        DataSource currentDataSource = CurrentThreadUtils.getDataSource();
        if (currentDataSource == null)
            return defaultDataSource;
        return currentDataSource;
    }
}
