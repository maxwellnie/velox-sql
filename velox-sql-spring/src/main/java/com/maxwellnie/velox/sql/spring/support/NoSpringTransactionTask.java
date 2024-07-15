package com.maxwellnie.velox.sql.spring.support;

import com.maxwellnie.velox.sql.core.distributed.TransactionTask;
import com.maxwellnie.velox.sql.core.meta.MetaData;
import com.maxwellnie.velox.sql.core.natives.exception.JDBCConnectionException;
import com.maxwellnie.velox.sql.spring.resource.CurrentJdbcSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>代理事务真正的实现，请注意，本代理事务在多数据源情境下非链式事务，而是直接对所有待处理事务进行操作，并不关注其中某个事务成功与否</p>
 * <p>多数据源情境下不建议使用！！！！！！！！！</p>
 *
 * @author Maxwell Nie
 */
public class NoSpringTransactionTask implements TransactionTask {
    private static final Logger logger = LoggerFactory.getLogger(NoSpringTransactionTask.class);
    /**
     * 待处理事务的元数据
     */
    private final List<MetaData> metaDataList = new LinkedList<>();

    /**
     * 添加待处理事务元数据
     *
     * @param metaData
     */
    @Override
    public void add(MetaData metaData) {
        metaDataList.add(metaData);
    }

    /**
     * 回滚事务
     *
     * @return
     */
    @Override
    public boolean rollback() {
        logger.debug("Transaction task start rollback.");
        int i = 0;
        for (MetaData metaData : metaDataList) {
            Connection connection = metaData.getProperty("connection");
            // 这里判断两种情况，一是当前框架事务被用户所代理（或者被框架提供的事务管理器所管理），二是当前框架事务被用户提供的spring事务管理器所管理
            if (connection != null && (CurrentJdbcSession.isOpenProxyTransaction() || DataSourceUtils.isConnectionTransactional(connection, metaData.getProperty("dataSource")))) {
                try {
                    logger.debug("rollback " + i++);
                    connection.rollback();
                } catch (Exception e) {
                    throw new JDBCConnectionException(e);
                }
            }
        }
        return true;
    }

    /**
     * 提交事务
     *
     * @return
     */
    @Override
    public boolean commit() {
        logger.debug("Transaction task start committing.");
        int i = 0;
        for (MetaData metaData : metaDataList) {
            Connection connection = metaData.getProperty("connection");
            if (connection != null && (CurrentJdbcSession.isOpenProxyTransaction() || DataSourceUtils.isConnectionTransactional(connection, metaData.getProperty("dataSource")))) {
                try {
                    logger.debug("commit " + i++);
                    connection.commit();
                } catch (Exception e) {
                    throw new JDBCConnectionException(e);
                }
            }
        }
        return true;
    }

    /**
     * 关闭事务
     */
    @Override
    public void close() {
        logger.debug("Transaction task start closing.");
        for (MetaData metaData : metaDataList) {
            Connection connection = metaData.getProperty("connection");
            if (connection != null) {
                try {
                    if (!CurrentJdbcSession.isOpenProxyTransaction())
                        DataSourceUtils.releaseConnection(connection, metaData.getProperty("dataSource"));
                    else
                        connection.close();
                } catch (Exception e) {
                    throw new JDBCConnectionException(e);
                }
            }
        }
    }
}
