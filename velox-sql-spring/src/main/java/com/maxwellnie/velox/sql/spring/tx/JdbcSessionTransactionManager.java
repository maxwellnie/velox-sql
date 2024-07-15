package com.maxwellnie.velox.sql.spring.tx;

import com.maxwellnie.velox.sql.core.natives.exception.JDBCConnectionException;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSession;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSessionFactory;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.impl.JdbcSessionProxy;
import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.impl.jdbc.Connections;
import com.maxwellnie.velox.sql.spring.resource.CurrentJdbcSession;
import com.maxwellnie.velox.sql.spring.resource.JdbcSessionHolder;
import com.maxwellnie.velox.sql.spring.resource.JdbcSessionUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 以JdbcSession为主要事务管理的对象的事务管理器
 *
 * @author Maxwell Nie
 */
public class JdbcSessionTransactionManager extends AbstractPlatformTransactionManager {
    /**
     * JdbcSession工厂
     */
    private final JdbcSessionFactory jdbcSessionFactory;
    /**
     * 只读事务的连接的预处理
     */
    private final Consumer<Connections.DataSourceAndConnection> prepareReadOnlyDataSourceConnection = dataSourceAndConnection -> {
        Statement statement = null;
        try {
            statement = dataSourceAndConnection.getConnection().createStatement();
            statement.execute("SET TRANSACTION ISOLATION LEVEL READ COMMITTED");

        } catch (SQLException e) {
            throw new JDBCConnectionException(e);
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException e) {
                throw new JDBCConnectionException(e);
            }

        }
    };
    /**
     * 事务结束消除预处理带来的影响
     */
    private final Consumer<Connections.DataSourceAndConnection> resetConnectionAfterTransaction = dataSourceAndConnection -> {
        DataSourceUtils.resetConnectionAfterTransaction(dataSourceAndConnection.getConnection(), dataSourceAndConnection.getLevel());
    };

    public JdbcSessionTransactionManager(JdbcSessionFactory jdbcSessionFactory) {
        this.jdbcSessionFactory = jdbcSessionFactory;
        setNestedTransactionAllowed(true);
    }

    @Override
    protected final Object doGetTransaction() throws TransactionException {
        if (jdbcSessionFactory.getHolderObject() != null) {
            CurrentJdbcSession.openProxyTransaction();
            JdbcSessionTransactionObject txObject = new JdbcSessionTransactionObject();
            JdbcSessionHolder holder = (JdbcSessionHolder) TransactionSynchronizationManager.getResource(jdbcSessionFactory);
            txObject.setJdbcSessionHolder(holder);
            return txObject;
        }
        return null;
    }

    @Override
    protected boolean isExistingTransaction(Object transaction) {
        if (transaction == null)
            return false;
        JdbcSessionTransactionObject txObject = (JdbcSessionTransactionObject) transaction;
        return (txObject.hasJdbcSessionHolder() && txObject.getJdbcSessionHolder().isSynchronizedWithTransaction());
    }

    @Override
    protected final void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
        JdbcSessionTransactionObject txObject = (JdbcSessionTransactionObject) transaction;
        JdbcSession jdbcSession = null;
        try {
            if (!txObject.hasJdbcSessionHolder() || txObject.getJdbcSessionHolder().isSynchronizedWithTransaction()) {
                jdbcSession = jdbcSessionFactory.produce();
                txObject.setJdbcSessionHolder(new JdbcSessionHolder(txObject.getJdbcSessionProxy()), true);
            } else {
                jdbcSession = txObject.getJdbcSessionHolder().getJdbcSession();
            }
            txObject.getJdbcSessionHolder().setSynchronizedWithTransaction(true);
            txObject.setJdbcSessionProxy(Objects.requireNonNull(CurrentJdbcSession.proxyCurrentSpringTransaction(jdbcSession, jdbcSessionFactory)));
            JdbcSessionProxy jdbcSessionProxy = txObject.getJdbcSessionProxy();
            if (jdbcSessionProxy.getAutoCommit()) {
                jdbcSessionProxy.setAutoCommit(false);
            }
            Consumer<Connections.DataSourceAndConnection> previousConnectionConsumer = dataSourceAndConnection -> {
                try {
                    dataSourceAndConnection.setLevel(DataSourceUtils.prepareConnectionForTransaction(dataSourceAndConnection.getConnection(), definition));
                } catch (SQLException e) {
                    throw new JDBCConnectionException(e);
                }
            };
            txObject.setPreviousConnectionConsumer(previousConnectionConsumer);
            jdbcSession.getTransaction().prepare(previousConnectionConsumer);
            prepareTransactionalJdbcSession(jdbcSession, definition);
            int timeout = determineTimeout(definition);
            if (timeout != TransactionDefinition.TIMEOUT_DEFAULT) {
                txObject.getJdbcSessionHolder().setTimeoutInSeconds(timeout);
            }
            if (txObject.getNewJdbcSessionHolder()) {
                TransactionSynchronizationManager.bindResource(jdbcSessionFactory, txObject.getJdbcSessionHolder());
            }
        } catch (Throwable ex) {
            if (txObject.getNewJdbcSessionHolder()) {
                JdbcSessionUtils.releaseJdbcSession(jdbcSession, jdbcSessionFactory);
                txObject.setJdbcSessionHolder(null);
                txObject.setNewJdbcSessionHolder(false);
            }
            throw new CannotCreateTransactionException("Could not open JdbcSession for transaction", ex);
        }
    }

    @Override
    protected final void doCommit(DefaultTransactionStatus status) throws TransactionException {
        CurrentJdbcSession.commit();
    }

    @Override
    protected final void doRollback(DefaultTransactionStatus status) throws TransactionException {
        CurrentJdbcSession.rollback();
    }

    /**
     * 方法被委托
     *
     * @param transaction transaction object returned by {@code doGetTransaction}
     * @return
     * @throws TransactionException
     * @see JdbcSessionUtils#JdbcSessionSynchronization
     */
    @Override
    protected final Object doSuspend(Object transaction) throws TransactionException {
        JdbcSessionTransactionObject txObject = (JdbcSessionTransactionObject) transaction;
        txObject.setJdbcSessionHolder(null);
        return TransactionSynchronizationManager.unbindResource(jdbcSessionFactory);
    }

    /**
     * 方法被委托
     *
     * @param transaction        transaction object returned by {@code doGetTransaction}
     * @param suspendedResources suspended resources returned by {@code doSuspend}
     * @throws TransactionException
     * @see JdbcSessionUtils#JdbcSessionSynchronization
     */
    @Override
    protected final void doResume(Object transaction, Object suspendedResources) throws TransactionException {
        TransactionSynchronizationManager.bindResource(jdbcSessionFactory, suspendedResources);
    }

    private final void doClose() {
        CurrentJdbcSession.close();
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        JdbcSessionTransactionObject txObject = (JdbcSessionTransactionObject) transaction;
        JdbcSessionProxy jdbcSessionProxy = txObject.getJdbcSessionProxy();
        jdbcSessionProxy.getTransaction().removePrepare(prepareReadOnlyDataSourceConnection);
        jdbcSessionProxy.getTransaction().removePrepare(txObject.getPreviousConnectionConsumer());
        try {
            jdbcSessionProxy.getTransaction().restore(resetConnectionAfterTransaction);
            jdbcSessionProxy.setAutoCommit(txObject.getOriginalAutoCommit());
        } catch (Throwable tx) {
            logger.error("Could not reset Connection to default cause:", tx);
        }
        if (txObject.getNewJdbcSessionHolder()) {
            TransactionSynchronizationManager.unbindResource(jdbcSessionFactory);
            JdbcSessionUtils.releaseJdbcSession(txObject.getJdbcSessionProxy().getTarget(), jdbcSessionFactory);
        }
        txObject.getJdbcSessionHolder().clear();
    }

    @Override
    protected void doSetRollbackOnly(DefaultTransactionStatus status) throws TransactionException {
        JdbcSessionTransactionObject txObject = (JdbcSessionTransactionObject) status.getTransaction();
        txObject.getJdbcSessionHolder().setRollbackOnly();
    }

    /**
     * 预处理事务
     *
     * @param jdbcSession
     * @param definition
     */
    protected void prepareTransactionalJdbcSession(JdbcSession jdbcSession, TransactionDefinition definition) {
        if (definition.isReadOnly()) {
            jdbcSession.getTransaction().prepare(prepareReadOnlyDataSourceConnection);
        }
    }
}
