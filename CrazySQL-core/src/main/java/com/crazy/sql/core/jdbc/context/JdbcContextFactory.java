package com.crazy.sql.core.jdbc.context;

/**
 * @author Akiba no ichiichiyoha
 */
public interface JdbcContextFactory {
    JdbcContext produce();
    JdbcContext produce(boolean autoCommit);
}
