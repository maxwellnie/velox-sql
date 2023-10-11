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

    Transaction produce(DataSource dataSource, boolean autoCommit, int level);

    Transaction produce(Connection connection);

    Transaction produce(Map<?, ?> map);

    Transaction produce(Object[] param);
}
