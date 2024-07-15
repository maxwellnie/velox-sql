package com.maxwellnie.velox.sql.core.distributed;

import com.maxwellnie.velox.sql.core.meta.MetaData;
import com.maxwellnie.velox.sql.core.natives.exception.JDBCConnectionException;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>代理事务真正的实现，请注意，本代理事务在多数据源情境下非链式事务，而是直接对所有待处理事务进行操作，并不关注其中某个事务成功与否</p>
 * <p>多数据源情境下不建议使用！！！！！！！！！</p>
 *
 * @author Maxwell Nie
 */
public class NoTransactionTask implements TransactionTask {
    private final List<MetaData> metaDataList = new LinkedList<>();

    @Override
    public void add(MetaData metaData) {
        metaDataList.add(metaData);
    }

    @Override
    public boolean rollback() {
        for (MetaData metaData : metaDataList) {
            Connection connection = metaData.getProperty("connection");
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (Exception e) {
                    throw new JDBCConnectionException(e);
                }
            }
        }
        return true;
    }

    @Override
    public boolean commit() {
        for (MetaData metaData : metaDataList) {
            Connection connection = metaData.getProperty("connection");
            if (connection != null) {
                try {
                    connection.commit();
                } catch (Exception e) {
                    throw new JDBCConnectionException(e);
                }
            }
        }
        return true;
    }

    @Override
    public void close() {
        for (MetaData metaData : metaDataList) {
            Connection connection = metaData.getProperty("connection");
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (Exception e) {
                    throw new JDBCConnectionException(e);
                }
            }
        }
    }
}
