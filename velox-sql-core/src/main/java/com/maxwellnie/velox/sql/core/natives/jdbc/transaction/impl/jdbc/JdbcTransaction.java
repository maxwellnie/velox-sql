package com.maxwellnie.velox.sql.core.natives.jdbc.transaction.impl.jdbc;

import com.maxwellnie.velox.sql.core.config.simple.SingletonConfiguration;
import com.maxwellnie.velox.sql.core.distributed.TransactionTask;
import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.Transaction;
import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.impl.base.ProxyCurrentDataSource;
import com.maxwellnie.velox.sql.core.natives.stream.ConsumerSet;
import com.maxwellnie.velox.sql.core.utils.reflect.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * @author Maxwell Nie
 */
public class JdbcTransaction implements Transaction {
    private static final Logger logger = LoggerFactory.getLogger(JdbcTransaction.class);
    private boolean autoCommit;
    private Integer level;
    private ProxyCurrentDataSource dataSource;
    private DataSource currentDataSource;
    private Connections connections = new Connections();
    private TransactionTask task;
    private ConsumerSet<Connections.DataSourceAndConnection> prepareConsumers = new ConsumerSet<>();

    public JdbcTransaction(ProxyCurrentDataSource dataSource, boolean autoCommit, int level) {
        this.autoCommit = autoCommit;
        this.dataSource = dataSource;
        this.level = level;
        try {
            this.task = ReflectionUtils.newInstance(SingletonConfiguration.getInstance().getTransactionTaskClass());
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public JdbcTransaction(Connection connection) {
        this.connections.add(null, connection);
        try {
            this.task = ReflectionUtils.newInstance(SingletonConfiguration.getInstance().getTransactionTaskClass());
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            this.autoCommit = connection.getAutoCommit();
            this.level = connection.getTransactionIsolation();
        } catch (SQLException e) {
            logger.error(e.getMessage() + "\t\n" + e.getCause());
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (!connections.isEmpty() && !autoCommit)
            task.rollback();
    }

    @Override
    public void commit() throws SQLException {
        if (!connections.isEmpty() && !autoCommit)
            task.commit();
    }

    @Override
    public void release() throws SQLException {
        if (!connections.isEmpty()) {
            task.close();
        }
    }

    @Override
    public Connections.DataSourceAndConnection getDataSourceAndConnection() throws SQLException {
        Connection cd;
        if (!connections.isEmpty()) {
            currentDataSource = dataSource.getCurrentDataSource();
            cd = currentDataSource.getConnection();
            cd.setAutoCommit(autoCommit);
            cd.setTransactionIsolation(level);
            connections.add(currentDataSource, cd);
        } else {
            if (!currentDataSource.equals(dataSource.getCurrentDataSource())) {
                currentDataSource = dataSource.getCurrentDataSource();
                cd = currentDataSource.getConnection();
                cd.setAutoCommit(autoCommit);
                connections.add(currentDataSource, cd);
            } else
                cd = connections.getConnection();
        }
        if (level != null)
            cd.setTransactionIsolation(level);
        Connections.DataSourceAndConnection dataSourceAndConnection = connections.get();
        prepareConsumers.accept(dataSourceAndConnection);
        return dataSourceAndConnection;
    }

    @Override
    public DataSource getHolderObject() {
        return dataSource.getCurrentDataSource();
    }

    @Override
    public TransactionTask getTransactionTask() {
        return task;
    }

    @Override
    public void prepare(Consumer<Connections.DataSourceAndConnection> consumer) {
        this.prepareConsumers.add(consumer);
    }

    @Override
    public void removePrepare(Consumer<Connections.DataSourceAndConnection> consumer) {
        this.prepareConsumers.remove(consumer);
    }

    @Override
    public void restore(Consumer<Connections.DataSourceAndConnection> consumer) {
        if (consumer != null && this.connections != null)
            for (Connections.DataSourceAndConnection dataSourceAndConnection : connections.all()) {
                consumer.accept(dataSourceAndConnection);
            }
    }
}
