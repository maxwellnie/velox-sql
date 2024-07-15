package com.maxwellnie.velox.sql.spring.listener.event;

import com.maxwellnie.velox.sql.core.config.Configuration;
import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * @author Maxwell Nie
 */
public class ContextCreationEvent extends SupportEvent {
    protected Configuration configuration;
    protected DataSource dataSource;
    protected TransactionFactory transactionFactory;

    public ContextCreationEvent(Configuration configuration, DataSource dataSource, TransactionFactory transactionFactory) {
        super(Arrays.asList(configuration, dataSource, transactionFactory));
        this.configuration = configuration;
        this.dataSource = dataSource;
        this.transactionFactory = transactionFactory;
    }

    public Configuration getBaseConfig() {
        return configuration;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public TransactionFactory getTransactionFactory() {
        return transactionFactory;
    }
}
