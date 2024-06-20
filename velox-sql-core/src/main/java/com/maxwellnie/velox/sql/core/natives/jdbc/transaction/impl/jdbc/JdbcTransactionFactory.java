package com.maxwellnie.velox.sql.core.natives.jdbc.transaction.impl.jdbc;

import com.maxwellnie.velox.sql.core.natives.exception.MethodNotSupportException;
import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.Transaction;
import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.impl.base.BaseTransactionFactory;
import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.impl.base.ProxyCurrentDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

/**
 * @author Maxwell Nie
 */
public class JdbcTransactionFactory extends BaseTransactionFactory {
    public JdbcTransactionFactory(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Transaction produce() {
        throw new MethodNotSupportException("This method is applied to a custom Transaction factory, the built-in factory does not support this method.");
    }

    @Override
    public Transaction produce(boolean autoCommit, int level) {
        return new JdbcTransaction(new ProxyCurrentDataSource(this.getDefaultDataSource()), autoCommit, level);
    }

    @Override
    public Transaction produce(Connection connection) {
        return new JdbcTransaction(connection);
    }

    @Override
    public Transaction produce(Map<?, ?> map) {
        throw new MethodNotSupportException("This method is applied to a custom Transaction factory, the built-in factory does not support this method.");
    }

    @Override
    public Transaction produce(Object[] param) {
        throw new MethodNotSupportException("This method is applied to a custom Transaction factory, the built-in factory does not support this method.");
    }

}
