package com.crazy.sql.core.jdbc.transaction.impl.jdbc;

import com.crazy.sql.core.jdbc.transaction.Transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Akiba no ichiichiyoha
 */
public class JdbcTransaction implements Transaction {
    private final boolean autoCommit;
    private DataSource dataSource;
    private final int level;
    private Connection connection;

    public JdbcTransaction(DataSource dataSource,boolean autoCommit,  int level) {
        this.autoCommit = autoCommit;
        this.dataSource = dataSource;
        this.level = level;
    }

    public JdbcTransaction(Connection connection) {
        this.connection = connection;
        try {
            this.autoCommit= connection.getAutoCommit();
            this.level=connection.getTransactionIsolation();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rollback() throws SQLException {
        if(connection!=null&&!autoCommit)
            connection.rollback();
    }

    @Override
    public void commit() throws SQLException {
        if(connection!=null&&!autoCommit)
            connection.commit();
    }

    @Override
    public void release() throws SQLException {
        if(connection!=null) {
            connection.setAutoCommit(true);
            connection.close();
        }
    }
    @Override
    public Connection getConnection() throws SQLException {
        if(connection==null){
            Connection cd=dataSource.getConnection();
            cd.setAutoCommit(autoCommit);
            cd.setTransactionIsolation(level);
            connection=cd;
        }
        return connection;
    }
}
