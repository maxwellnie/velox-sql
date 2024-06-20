package com.maxwellnie.velox.sql.core.natives.jdbc.transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

/**
 * 事务工厂
 *
 * @author Maxwell Nie
 */
public interface TransactionFactory {
    Transaction produce();

    /**
     * it's mainly method,must be implemented.
     *
     * @param autoCommit
     * @param level
     * @return Transaction Object
     */
    Transaction produce(boolean autoCommit, int level);

    Transaction produce(Connection connection);

    Transaction produce(Map<?, ?> map);

    Transaction produce(Object[] param);

    DataSource getDefaultDataSource();
}
