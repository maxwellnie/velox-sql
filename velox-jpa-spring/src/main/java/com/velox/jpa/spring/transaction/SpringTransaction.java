package com.velox.jpa.spring.transaction;

import com.maxwellnie.velox.jpa.core.jdbc.transaction.Transaction;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public class SpringTransaction implements Transaction {
    private final DataSource dataSource;
    private boolean autoCommit;
    private Connection connection;

    public SpringTransaction(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void rollback() throws SQLException {
        if (connection != null && !DataSourceUtils.isConnectionTransactional(connection, dataSource) && !autoCommit)
            connection.rollback();
    }

    @Override
    public void commit() throws SQLException {
        if (connection != null && !DataSourceUtils.isConnectionTransactional(connection, dataSource) && !autoCommit)
            connection.commit();
    }

    @Override
    public void release() throws SQLException {
        if (connection != null && dataSource != null) {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null) {
            Connection cd = DataSourceUtils.getConnection(dataSource);
            autoCommit = cd.getAutoCommit();
            connection = cd;
        }
        return connection;
    }
}
