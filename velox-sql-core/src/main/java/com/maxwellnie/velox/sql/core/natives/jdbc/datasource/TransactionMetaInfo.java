package com.maxwellnie.velox.sql.core.natives.jdbc.datasource;

/**
 * @author Maxwell Nie
 */
public interface TransactionMetaInfo {
    void setTransactionIsolation(int level);

    Integer getTransactionIsolation();

    void readOnly(boolean flag);

    Boolean isReadOnly();

    long getTimeout();

    void setTimeout(long timeout);
}
