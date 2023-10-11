package com.maxwellnie.velox.jpa.core.jdbc.transaction.impl.jdbc;

import com.maxwellnie.velox.jpa.core.jdbc.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public class JdbcTransaction implements Transaction {
    private static final Logger logger= LoggerFactory.getLogger(JdbcTransaction.class);
    private boolean autoCommit;
    private int level;
    private DataSource dataSource;
    private Connection connection;

    public JdbcTransaction(DataSource dataSource, boolean autoCommit, int level) {
        this.autoCommit = autoCommit;
        this.dataSource = dataSource;
        this.level = level;
    }

    public JdbcTransaction(Connection connection) {
        this.connection = connection;
        try {
            this.autoCommit = connection.getAutoCommit();
            this.level = connection.getTransactionIsolation();
        } catch (SQLException e) {
            logger.error(e.getMessage()+"\t\n"+e.getCause());
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (connection != null && !autoCommit)
            connection.rollback();
    }

    @Override
    public void commit() throws SQLException {
        if (connection != null && !autoCommit)
            connection.commit();
    }

    @Override
    public void release() throws SQLException {
        if (connection != null) {
            connection.setAutoCommit(true);
            connection.close();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            Connection cd = dataSource.getConnection();
            cd.setAutoCommit(autoCommit);
            cd.setTransactionIsolation(level);
            connection = cd;
        }
        return connection;
    }
}
