package com.maxwellnie.velox.sql.core.natives.jdbc.session;

import com.maxwellnie.velox.sql.core.cache.transactional.CacheTransactional;
import com.maxwellnie.velox.sql.core.natives.exception.JdbcContextException;
import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.Transaction;
import com.maxwellnie.velox.sql.core.natives.task.TaskQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class DefaultJdbcSession implements JdbcSession {
    private static final Logger logger = LoggerFactory.getLogger(DefaultJdbcSession.class);
    private CacheTransactional cacheTransactional;
    private Transaction transaction;
    private boolean autoCommit;
    private boolean closed = false;
    private TaskQueue taskQueue;
    private final List<Listener> listeners = Collections.synchronizedList(new ArrayList<>());

    public DefaultJdbcSession() {
        requestTransaction(true);
    }

    public DefaultJdbcSession(Transaction transaction, boolean autoCommit, TaskQueue taskQueue) {
        this.transaction = transaction;
        this.autoCommit = autoCommit;
        this.taskQueue = taskQueue;
        requestTransaction(autoCommit);
    }
    private void requestTransaction(boolean autoCommit) {
        if (autoCommit)
            this.cacheTransactional = null;
        else
            this.cacheTransactional = new CacheTransactional();
    }

    @Override
    public boolean getAutoCommit() {
        return this.autoCommit;
    }

    @Override
    public void setAutoCommit(boolean flag) {
        this.autoCommit = flag;
    }
    @Override
    public TaskQueue getTaskQueue() {
        return taskQueue;
    }
    @Override
    public void close() {
        for (Listener listener : listeners) {
            listener.beforeClose(this);
        }
        try {
            transaction.release();
            cacheTransactional.commit();
            cacheTransactional.clear();
            this.closed = true;
            for (Listener listener : listeners) {
                listener.afterClose(this);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage() + "\tt\n" + e.getCause());
        }
    }

    @Override
    public void close(boolean commit) {
        for (Listener listener : listeners) {
            listener.beforeClose(this);
        }
        if(commit)
            close();
        else {
            try {
                transaction.release();
                this.closed = true;
                for (Listener listener : listeners) {
                    listener.afterClose(this);
                }
            } catch (SQLException e) {
                logger.error(e.getMessage() + "\tt\n" + e.getCause());
            }
        }

    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }
    public CacheTransactional getDirtyManager() {
        return cacheTransactional;
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    public Transaction getTransaction() {
        return transaction;
    }
    @Override
    public void commit() {
        if (!autoCommit) {
            for (Listener listener : listeners) {
                listener.beforeCommit(this);
            }
        }
        if (closed) {
            cacheTransactional.clear();
            throw new JdbcContextException("The JdbcSession " + this + " is closed.but it need commit data.");
        }
        logger.debug(cacheTransactional.toString());
        cacheTransactional.commit();
        try {
            transaction.commit();
            logger.debug(this + " is commit");
            for (Listener listener : listeners) {
                listener.afterCommit(this);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage() + "\tt\n" + e.getCause());
        }
    }

    @Override
    public void rollback() {
        if (!closed) {
            for (Listener listener : listeners) {
                listener.beforeRollback(this);
            }
            cacheTransactional.rollback();
            try {
                transaction.rollback();
                logger.debug(this + " is rollback");
                for (Listener listener : listeners) {
                    listener.afterRollback(this);
                }
            } catch (SQLException e) {
                logger.error(e.getMessage() + "\tt\n" + e.getCause());
            }
        }
    }
    public static interface Listener{
        default void beforeCommit(JdbcSession session){}
        default void beforeRollback(JdbcSession session){}
        default void beforeClose(JdbcSession session){}
        default void afterCommit(JdbcSession session){}
        default void afterRollback(JdbcSession session){}
        default void afterClose(JdbcSession session){}
    }
}
