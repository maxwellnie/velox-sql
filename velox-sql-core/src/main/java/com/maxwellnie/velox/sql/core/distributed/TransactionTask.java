package com.maxwellnie.velox.sql.core.distributed;

import com.maxwellnie.velox.sql.core.meta.MetaData;

/**
 * @author Maxwell Nie
 */
public interface TransactionTask {
    void add(MetaData metaData);

    boolean rollback();

    boolean commit();

    void close();
}
