package com.maxwellnie.velox.sql.core.natives.jdbc.transaction.impl.base;

import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.TransactionFactory;

import javax.sql.DataSource;

/**
 * @author Maxwell Nie
 */
public abstract class BaseTransactionFactory implements TransactionFactory {
    private final DataSource defaultDataSource;

    public BaseTransactionFactory(DataSource defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
    }

    @Override
    public DataSource getDefaultDataSource() {
        return this.defaultDataSource;
    }
}
