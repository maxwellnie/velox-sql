package com.maxwellnie.velox.sql.spring.resource;

import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSession;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSessionFactory;
import com.maxwellnie.velox.sql.core.natives.jdbc.context.Context;
import com.maxwellnie.velox.sql.spring.transaction.SpringTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.springframework.util.Assert.notNull;

/**
 * 用于配合Spring事务管理
 *
 * @author Maxwell Nie
 */
public abstract class JdbcSessionUtils {
    /**
     * Order value for TransactionSynchronization objects that clean up JdbcSession.
     *
     * @see DataSourceUtils#CONNECTION_SYNCHRONIZATION_ORDER
     */
    public static final int JDBC_CONTEXT_SYNCHRONIZATION_ORDER = DataSourceUtils.CONNECTION_SYNCHRONIZATION_ORDER - 0x14;
    private static final Logger logger = LoggerFactory.getLogger(JdbcSessionUtils.class);

    /**
     * 当JdbcContext需要被spring管理时，获取一个被spring管理的JdbcContext，否则则返回未被管理的JdbcContext。
     *
     * @param jdbcSessionFactory
     * @return JdbcContext实例
     * @see DataSourceUtils#getConnection(DataSource)
     */
    public static JdbcSession getJdbcContext(JdbcSessionFactory jdbcSessionFactory) {
        notNull(jdbcSessionFactory, "JdbcSessionFactory must be not null");
        JdbcSessionHolder jdbcSessionHolder = (JdbcSessionHolder) TransactionSynchronizationManager.getResource(jdbcSessionFactory);
        JdbcSession holderContext = requestJdbcContext(jdbcSessionHolder);
        if (holderContext != null) {
            return holderContext;
        } else {
            holderContext = jdbcSessionFactory.produce();
            registerJdbcContextHolder(jdbcSessionFactory, holderContext);
        }
        logger.debug("Get JdbcContext [{}] from JdbcSessionFactory", holderContext);
        return holderContext;
    }

    /**
     * 请求一个被spring管理的JdbcContext
     *
     * @param holder
     * @return 被spring管理的JdbcContext
     * @see PlatformTransactionManager
     */
    private static JdbcSession requestJdbcContext(JdbcSessionHolder holder) {
        if (holder != null && holder.isSynchronizedWithTransaction()) {
            holder.requested();
            return holder.getJdbcSession();
        }
        return null;
    }

    /**
     * 释放JdbcContext在spring的资源
     *
     * @param jdbcSession
     * @param jdbcSessionFactory
     */
    public static void releaseJdbcContext(JdbcSession jdbcSession, JdbcSessionFactory jdbcSessionFactory) {
        notNull(jdbcSession, "JdbcSession must be not null");
        notNull(jdbcSessionFactory, "JdbcSessionFactory must be not null");

        JdbcSessionHolder holder = (JdbcSessionHolder) TransactionSynchronizationManager.getResource(jdbcSessionFactory);
        if ((holder != null) && (holder.getJdbcSession() == jdbcSession)) {
            logger.debug("Releasing transactional JdbcSession - " + jdbcSession);
            holder.released();
        } else {
            logger.debug("Closing non transactional JdbcSession - " + jdbcSession);
            jdbcSession.close();
        }
    }

    /**
     * 注册JdbcContext资源到spring
     *
     * @param jdbcSessionFactory
     * @param jdbcSession
     * @see DataSourceUtils#doGetConnection(DataSource)
     */
    private static void registerJdbcContextHolder(JdbcSessionFactory jdbcSessionFactory, JdbcSession jdbcSession) {
        JdbcSessionHolder holder;
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            Context context = jdbcSessionFactory.getHolderObject();

            if (context.getTransactionFactory() instanceof SpringTransactionFactory) {
                holder = new JdbcSessionHolder(jdbcSession);
                TransactionSynchronizationManager.bindResource(jdbcSessionFactory, holder);
                TransactionSynchronizationManager
                        .registerSynchronization(new JdbcSessionSynchronization(holder, jdbcSessionFactory));
                holder.setSynchronizedWithTransaction(true);
                holder.requested();
                logger.debug("The JdbcSession " + jdbcSession + " is transactional.");
            } else {
                if (TransactionSynchronizationManager.getResource(context.getTransactionFactory().getDefaultDataSource()) == null) {
                    logger.warn("Registered failed,DataSource is not transactional");
                } else {
                    throw new TransientDataAccessResourceException(
                            "Context must be holder a SpringTransactionFactory in order to use Spring transaction synchronization");
                }
            }
        } else {
            logger.warn("Registered failed,synchronization is not active");
        }

    }

    /**
     * 检测JdbcContext是否支持spring事务管理
     *
     * @param jdbcSession
     * @param contextFactory
     * @return Boolean
     * @see DataSourceUtils#isConnectionTransactional(Connection, DataSource)
     */
    public static boolean isJdbcContextTransactional(JdbcSession jdbcSession, JdbcSessionFactory contextFactory) {
        notNull(jdbcSession, "JdbcSession must be not null");
        notNull(contextFactory, "JdbcSessionFactory must be not null");

        JdbcSessionHolder holder = (JdbcSessionHolder) TransactionSynchronizationManager.getResource(contextFactory);

        return (holder != null) && (holder.getJdbcSession() == jdbcSession);
    }

    /**
     * JdbcSession事务同步适配器
     *
     * @see TransactionSynchronizationAdapter
     * @see DataSourceUtils.ConnectionSynchronization
     */
    private static class JdbcSessionSynchronization extends TransactionSynchronizationAdapter {
        private final JdbcSessionHolder jdbcSessionHolder;
        private final JdbcSessionFactory jdbcSessionFactory;
        private boolean isActive = true;

        public JdbcSessionSynchronization(JdbcSessionHolder jdbcSessionHolder, JdbcSessionFactory jdbcSessionFactory) {
            notNull(jdbcSessionHolder, "ConnectionHolder must be not null");
            notNull(jdbcSessionFactory, "JdbcSessionFactory must be not null");
            this.jdbcSessionHolder = jdbcSessionHolder;
            this.jdbcSessionFactory = jdbcSessionFactory;
        }

        /**
         * 提交前要将JdbcContext所携带的脏数据提交
         *
         * @param readOnly whether the transaction is defined as read-only transaction
         */
        @Override
        public void beforeCommit(boolean readOnly) {
            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                logger.debug("Transaction synchronization committing - " + jdbcSessionHolder.getJdbcSession());
                this.jdbcSessionHolder.getJdbcSession().commit();
            }
        }

        /**
         * 防止多线程操作事务管理器时，出现资源访问冲突
         */
        @Override
        public void beforeCompletion() {
            if (!this.jdbcSessionHolder.isOpen()) {
                TransactionSynchronizationManager.unbindResource(jdbcSessionFactory);
                this.isActive = false;
                this.jdbcSessionHolder.getJdbcSession().close();
            }
        }

        /**
         * 提交成功记录
         */
        @Override
        public void afterCommit() {
            logger.debug("Completed commit data to database - " + jdbcSessionHolder.getJdbcSession());
        }

        /**
         * 事务结束时如果资源仍处于活动状态，需要解绑和释放资源。
         *
         * @param status completion status according to the {@code STATUS_*} constants
         */
        @Override
        public void afterCompletion(int status) {
            if (this.isActive) {
                TransactionSynchronizationManager.unbindResourceIfPossible(jdbcSessionFactory);
                this.isActive = false;
                this.jdbcSessionHolder.getJdbcSession().close();
            }
            this.jdbcSessionHolder.reset();
        }

        /**
         * 因为jdbcContext包装了Connection，所以应该将JdbcContext在事务同步器顺序调到Connection事务的前面
         *
         * @return 事务同步器需要清理JdbcContext时，需要获取到它的序号，按序号清理，这里是他的序号
         * @see #JDBC_CONTEXT_SYNCHRONIZATION_ORDER
         */
        @Override
        public int getOrder() {
            return JDBC_CONTEXT_SYNCHRONIZATION_ORDER;
        }

        /**
         * 暂停状态时应解绑绑定好的资源
         */
        @Override
        public void suspend() {
            if (this.isActive)
                TransactionSynchronizationManager.unbindResource(this.jdbcSessionFactory);
        }

        /**
         * 暂停状态变为恢复，需要重新绑定资源。
         */
        @Override
        public void resume() {
            if (this.isActive)
                TransactionSynchronizationManager.bindResource(this.jdbcSessionFactory, this.jdbcSessionHolder);
        }
    }
}
