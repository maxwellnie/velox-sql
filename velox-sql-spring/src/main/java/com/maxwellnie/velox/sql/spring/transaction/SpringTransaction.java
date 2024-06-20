package com.maxwellnie.velox.sql.spring.transaction;

import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.Transaction;
import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.impl.base.ProxyCurrentDataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Spring管理的事务
 * @author Maxwell Nie
 */
public class SpringTransaction implements Transaction {
    private final ProxyCurrentDataSource dataSource;
    private boolean autoCommit;
    private Connection connection;
    private DataSource currentDataSource;

    public SpringTransaction(ProxyCurrentDataSource dataSource) {
        this.dataSource = dataSource;
        this.currentDataSource = dataSource.getCurrentDataSource();
    }

    @Override
    public void rollback() throws SQLException {
        if (connection != null && !DataSourceUtils.isConnectionTransactional(connection, dataSource.getCurrentDataSource()) && !autoCommit)
            connection.rollback();
    }

    @Override
    public void commit() throws SQLException {
        if (connection != null && !DataSourceUtils.isConnectionTransactional(connection, dataSource.getCurrentDataSource()) && !autoCommit)
            connection.commit();
    }

    @Override
    public void release() {
        if (connection != null && dataSource != null) {
            DataSourceUtils.releaseConnection(connection, dataSource.getCurrentDataSource());
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null) {
            Connection cd = DataSourceUtils.getConnection(dataSource.getCurrentDataSource());
            autoCommit = cd.getAutoCommit();
            connection = cd;
            currentDataSource = dataSource.getCurrentDataSource();
        }else {
            if (currentDataSource == null || !currentDataSource.equals(dataSource.getCurrentDataSource())){
                currentDataSource = dataSource.getCurrentDataSource();
                connection = DataSourceUtils.getConnection(currentDataSource);
            }
            // undo
        }
        return connection;
    }

    @Override
    public DataSource getHolderObject() {
        return dataSource.getCurrentDataSource();
    }
}
