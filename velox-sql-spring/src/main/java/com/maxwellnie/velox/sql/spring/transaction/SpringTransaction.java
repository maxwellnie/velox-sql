package com.maxwellnie.velox.sql.spring.transaction;

import com.maxwellnie.velox.sql.core.config.simple.SingletonConfiguration;
import com.maxwellnie.velox.sql.core.distributed.TransactionTask;
import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.Transaction;
import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.impl.base.ProxyCurrentDataSource;
import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.impl.jdbc.Connections;
import com.maxwellnie.velox.sql.core.natives.stream.ConsumerSet;
import com.maxwellnie.velox.sql.core.utils.reflect.ReflectionUtils;
import com.maxwellnie.velox.sql.spring.resource.CurrentJdbcSession;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * Spring管理的事务
 *
 * @author Maxwell Nie
 */
public class SpringTransaction implements Transaction {
    private final ProxyCurrentDataSource dataSource;
    private boolean autoCommit;
    private DataSource currentDataSource;
    private Connections connections = new Connections();
    private TransactionTask task;
    private ConsumerSet<Connections.DataSourceAndConnection> prepareConsumers = new ConsumerSet<>();

    public SpringTransaction(ProxyCurrentDataSource dataSource) {
        this.dataSource = dataSource;
        this.currentDataSource = dataSource.getCurrentDataSource();
        try {
            this.task = ReflectionUtils.newInstance(SingletonConfiguration.getInstance().getTransactionTaskClass());
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            e.printStackTrace();
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
    public void release() {
        if (connections.isEmpty() && dataSource != null) {
            task.close();
        }
    }

    public Connections.DataSourceAndConnection getDataSourceAndConnection() throws SQLException {
        Connection cd;
        if (connections.isEmpty()) {
            if (!CurrentJdbcSession.isOpenProxyTransaction())
                cd = DataSourceUtils.getConnection(dataSource.getCurrentDataSource());
            else
                cd = dataSource.getCurrentDataSource().getConnection();
            cd.setAutoCommit(autoCommit);
            connections.add(currentDataSource, cd);
            currentDataSource = dataSource.getCurrentDataSource();
        } else {
            if (currentDataSource == null || !currentDataSource.equals(dataSource.getCurrentDataSource())) {
                currentDataSource = dataSource.getCurrentDataSource();
                cd = currentDataSource.getConnection();
                cd.setAutoCommit(autoCommit);
                connections.add(currentDataSource, cd);
            } else
                cd = connections.getConnection();
        }
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
