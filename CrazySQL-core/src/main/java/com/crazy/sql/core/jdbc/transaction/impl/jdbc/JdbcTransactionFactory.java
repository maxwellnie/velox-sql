package com.crazy.sql.core.jdbc.transaction.impl.jdbc;

import com.crazy.sql.core.exception.MethodNotSupportException;
import com.crazy.sql.core.jdbc.transaction.Transaction;
import com.crazy.sql.core.jdbc.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

/**
 * @author Akiba no ichiichiyoha
 */
public class JdbcTransactionFactory implements TransactionFactory {
    @Override
    public Transaction produce(DataSource dataSource) {
        throw new MethodNotSupportException("This method is applied to a custom Transaction factory, the built-in factory does not support this method.");
    }

    @Override
    public Transaction produce(DataSource dataSource,boolean autoCommit,int level) {
        return new JdbcTransaction(dataSource,autoCommit,level);
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
