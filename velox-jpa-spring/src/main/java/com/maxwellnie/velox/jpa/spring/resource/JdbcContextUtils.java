package com.maxwellnie.velox.jpa.spring.resource;

import com.maxwellnie.velox.jpa.core.dao.support.env.Environment;
import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContextFactory;
import com.maxwellnie.velox.jpa.spring.transaction.SpringTransactionFactory;
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
public abstract class JdbcContextUtils {
    /**
     * Order value for TransactionSynchronization objects that clean up JdbcContext.
     *
     * @see DataSourceUtils#CONNECTION_SYNCHRONIZATION_ORDER
     */
    public static final int JDBC_CONTEXT_SYNCHRONIZATION_ORDER = DataSourceUtils.CONNECTION_SYNCHRONIZATION_ORDER - 0x14;
    private static final Logger logger = LoggerFactory.getLogger(JdbcContextUtils.class);

    /**
     * 当JdbcContext需要被spring管理时，获取一个被spring管理的JdbcContext，否则则返回未被管理的JdbcContext。
     *
     * @param jdbcContextFactory
     * @return JdbcContext实例
     * @see DataSourceUtils#getConnection(DataSource)
     */
    public static JdbcContext getJdbcContext(JdbcContextFactory jdbcContextFactory) {
        notNull(jdbcContextFactory, "JdbcContextFactory must be not null");
        JdbcContextHolder jdbcContextHolder = (JdbcContextHolder) TransactionSynchronizationManager.getResource(jdbcContextFactory);
        JdbcContext holderContext = requestJdbcContext(jdbcContextHolder);
        if (holderContext != null) {
            return holderContext;
        } else {
            holderContext = jdbcContextFactory.produce();
            registerJdbcContextHolder(jdbcContextFactory, holderContext);
        }
        return holderContext;
    }

    /**
     * 请求一个被spring管理的JdbcContext
     *
     * @param holder
     * @return 被spring管理的JdbcContext
     * @see PlatformTransactionManager
     */
    private static JdbcContext requestJdbcContext(JdbcContextHolder holder) {
        if (holder != null && holder.isSynchronizedWithTransaction()) {
            holder.requested();
            return holder.getContext();
        }
        return null;
    }

    /**
     * 释放JdbcContext在spring的资源
     *
     * @param jdbcContext
     * @param jdbcContextFactory
     */
    public static void releaseJdbcContext(JdbcContext jdbcContext, JdbcContextFactory jdbcContextFactory) {
        notNull(jdbcContext, "JdbcContext must be not null");
        notNull(jdbcContextFactory, "JdbcContextFactory must be not null");

        JdbcContextHolder holder = (JdbcContextHolder) TransactionSynchronizationManager.getResource(jdbcContextFactory);
        if ((holder != null) && (holder.getContext() == jdbcContext)) {
            logger.debug("Releasing transactional JdbcContext - " + jdbcContext);
            holder.released();
        } else {
            logger.debug("Closing non transactional JdbcContext - " + jdbcContext);
            jdbcContext.close();
        }
    }

    /**
     * 注册JdbcContext资源到spring
     *
     * @param jdbcContextFactory
     * @param jdbcContext
     * @see DataSourceUtils#doGetConnection(DataSource)
     */
    private static void registerJdbcContextHolder(JdbcContextFactory jdbcContextFactory, JdbcContext jdbcContext) {
        JdbcContextHolder holder;
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            Environment environment = jdbcContextFactory.getEnvironment();

            if (environment.getTransactionFactory() instanceof SpringTransactionFactory) {
                holder = new JdbcContextHolder(jdbcContext);
                TransactionSynchronizationManager.bindResource(jdbcContextFactory, holder);
                TransactionSynchronizationManager
                        .registerSynchronization(new JdbcContextSynchronization(holder, jdbcContextFactory));
                holder.setSynchronizedWithTransaction(true);
                holder.requested();
                logger.debug("The JdbcContext " + jdbcContext + " is transactional.");
            } else {
                if (TransactionSynchronizationManager.getResource(environment.getDataSource()) == null) {
                    logger.warn("Registered failed,DataSource is not transactional");
                } else {
                    throw new TransientDataAccessResourceException(
                            "Environment must be holder a SpringTransactionFactory in order to use Spring transaction synchronization");
                }
            }
        } else {
            logger.warn("Registered failed,synchronization is not active");
        }

    }

    /**
     * 检测JdbcContext是否支持spring事务管理
     *
     * @param jdbcContext
     * @param contextFactory
     * @return Boolean
     * @see DataSourceUtils#isConnectionTransactional(Connection, DataSource)
     */
    public static boolean isJdbcContextTransactional(JdbcContext jdbcContext, JdbcContextFactory contextFactory) {
        notNull(jdbcContext, "JdbcContext must be not null");
        notNull(contextFactory, "JdbcContextFactory must be not null");

        JdbcContextHolder holder = (JdbcContextHolder) TransactionSynchronizationManager.getResource(contextFactory);

        return (holder != null) && (holder.getContext() == jdbcContext);
    }

    /**
     * JdbcContext事务同步适配器
     *
     * @see TransactionSynchronizationAdapter
     * @see DataSourceUtils.ConnectionSynchronization
     */
    private static class JdbcContextSynchronization extends TransactionSynchronizationAdapter {
        private final JdbcContextHolder jdbcContextHolder;
        private final JdbcContextFactory jdbcContextFactory;
        private boolean isActive = true;

        public JdbcContextSynchronization(JdbcContextHolder jdbcContextHolder, JdbcContextFactory jdbcContextFactory) {
            notNull(jdbcContextHolder, "JdbcContextHolder must be not null");
            notNull(jdbcContextFactory, "JdbcContextFactory must be not null");
            this.jdbcContextHolder = jdbcContextHolder;
            this.jdbcContextFactory = jdbcContextFactory;
        }

        /**
         * 提交前要将JdbcContext所携带的脏数据提交
         *
         * @param readOnly whether the transaction is defined as read-only transaction
         */
        @Override
        public void beforeCommit(boolean readOnly) {
            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                logger.debug("Transaction synchronization committing - " + jdbcContextHolder.getContext());
                this.jdbcContextHolder.getContext().commit();
            }
        }

        /**
         * 防止多线程操作事务管理器时，出现资源访问冲突
         */
        @Override
        public void beforeCompletion() {
            if (!this.jdbcContextHolder.isOpen()) {
                TransactionSynchronizationManager.unbindResource(jdbcContextFactory);
                this.isActive = false;
                this.jdbcContextHolder.getContext().close();
            }
        }

        /**
         * 提交成功记录
         */
        @Override
        public void afterCommit() {
            logger.debug("Completed commit data to database - " + jdbcContextHolder.getContext());
        }

        /**
         * 事务结束时如果资源仍处于活动状态，需要解绑和释放资源。
         *
         * @param status completion status according to the {@code STATUS_*} constants
         */
        @Override
        public void afterCompletion(int status) {
            if (this.isActive) {
                TransactionSynchronizationManager.unbindResourceIfPossible(jdbcContextFactory);
                this.isActive = false;
                this.jdbcContextHolder.getContext().close();
            }
            this.jdbcContextHolder.reset();
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
                TransactionSynchronizationManager.unbindResource(this.jdbcContextFactory);
        }

        /**
         * 暂停状态变为恢复，需要重新绑定资源。
         */
        @Override
        public void resume() {
            if (this.isActive)
                TransactionSynchronizationManager.bindResource(this.jdbcContextFactory, this.jdbcContextHolder);
        }
    }
}
