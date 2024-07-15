package com.maxwellnie.velox.sql.spring.listener.event;

import com.maxwellnie.velox.sql.core.natives.jdbc.context.Context;
import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * @author Maxwell Nie
 */
public class PostContextCreationEvent extends SupportEvent {
    protected Context context;
    protected DataSource dataSource;
    protected TransactionFactory transactionFactory;

    public PostContextCreationEvent(Context context, DataSource dataSource, TransactionFactory transactionFactory) {
        super(Arrays.asList(context, dataSource, transactionFactory));
        this.context = context;
        this.dataSource = dataSource;
        this.transactionFactory = transactionFactory;
    }

    public Context getEnvironment() {
        return context;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public TransactionFactory getTransactionFactory() {
        return transactionFactory;
    }
}
