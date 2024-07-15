package com.maxwellnie.velox.sql.core.natives.jdbc.transaction;

import com.maxwellnie.velox.sql.core.distributed.TransactionTask;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.DataSourceAndConnectionHolder;
import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.impl.jdbc.Connections;
import com.maxwellnie.velox.sql.core.natives.resource.ResourceHolder;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * DaoImpl的事务
 *
 * @author Maxwell Nie
 */
public interface Transaction extends DataSourceAndConnectionHolder, ResourceHolder<DataSource> {
    /**
     * 数据回滚
     *
     * @throws SQLException
     */
    void rollback() throws SQLException;

    /**
     * 数据提交
     *
     * @throws SQLException
     */
    void commit() throws SQLException;

    /**
     * 释放连接
     *
     * @throws SQLException
     */
    void release() throws SQLException;

    TransactionTask getTransactionTask();

    /**
     * 预处理Connection
     *
     * @param consumer
     */
    void prepare(Consumer<Connections.DataSourceAndConnection> consumer);

    /**
     * 移除预处理Connection
     *
     * @param consumer
     */
    void removePrepare(Consumer<Connections.DataSourceAndConnection> consumer);

    /**
     * 恢复预处理前的Connection
     *
     * @param consumer
     */
    void restore(Consumer<Connections.DataSourceAndConnection> consumer);
}
