package com.velox.jpa.spring.resource;

import com.velox.jpa.spring.transaction.SpringTransactionFactory;
import com.maxwellnie.vleox.jpa.core.dao.support.env.Environment;
import com.maxwellnie.vleox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.vleox.jpa.core.jdbc.context.JdbcContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.springframework.util.Assert.notNull;

/**
 * 用于配合Spring事务管理
 *
 * @author Maxwell Nie
 */
public class JdbcContextUtils {
    public static final int JDBC_CONTEXT_SYNCHRONIZATION_ORDER = DataSourceUtils.CONNECTION_SYNCHRONIZATION_ORDER - 0x14;
    private static final Logger logger = LoggerFactory.getLogger(JdbcContextUtils.class);

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

    private static JdbcContext requestJdbcContext(JdbcContextHolder holder) {
        if (holder != null && holder.isSynchronizedWithTransaction()) {
            holder.requested();
            return holder.getContext();
        }
        return null;
    }

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

    public static boolean isJdbcContextTransactional(JdbcContext jdbcContext, JdbcContextFactory contextFactory) {
        notNull(jdbcContext, "JdbcContext must be not null");
        notNull(contextFactory, "JdbcContextFactory must be not null");

        JdbcContextHolder holder = (JdbcContextHolder) TransactionSynchronizationManager.getResource(contextFactory);

        return (holder != null) && (holder.getContext() == jdbcContext);
    }

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
         * 因为jdbcContext包装了Connection，所以应该将事务的执行顺序调到Connection事务的前面
         *
         * @return
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
         * 结束事务暂停状态后恢复，需要重新绑定资源。
         */
        @Override
        public void resume() {
            if (this.isActive)
                TransactionSynchronizationManager.bindResource(this.jdbcContextFactory, this.jdbcContextHolder);
        }
    }
}
