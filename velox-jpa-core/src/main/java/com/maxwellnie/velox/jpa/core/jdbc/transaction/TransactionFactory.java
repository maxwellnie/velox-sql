package com.maxwellnie.velox.jpa.core.jdbc.transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

/**
 * 事务工厂
 *
 * @author Maxwell Nie
 */
public interface TransactionFactory {
    Transaction produce(DataSource dataSource);

    /**
     * it's mainly method,must be implemented.
     *
     * @param dataSource
     * @param autoCommit
     * @param level
     * @return Transaction Object
     */
    Transaction produce(DataSource dataSource, boolean autoCommit, int level);

    Transaction produce(Connection connection);

    Transaction produce(Map<?, ?> map);

    Transaction produce(Object[] param);
}
