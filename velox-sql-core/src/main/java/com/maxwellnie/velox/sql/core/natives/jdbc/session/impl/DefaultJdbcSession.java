package com.maxwellnie.velox.sql.core.natives.jdbc.session.impl;

import com.maxwellnie.velox.sql.core.cache.transactional.CacheTransactional;
import com.maxwellnie.velox.sql.core.natives.exception.JdbcSessionException;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSession;
import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.Transaction;
import com.maxwellnie.velox.sql.core.natives.task.TaskQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

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
        try {
            transaction.release();
            cacheTransactional.commit();
            cacheTransactional.clear();
            this.closed = true;
        } catch (SQLException e) {
            logger.error(e.getMessage() + "\tt\n" + e.getCause());
        }
    }

    @Override
    public void close(boolean commit) {
        if (commit)
            close();
        else {
            try {
                transaction.release();
                cacheTransactional.rollback();
                cacheTransactional.clear();
                this.closed = true;
            } catch (SQLException e) {
                logger.error(e.getMessage() + "\tt\n" + e.getCause());
            }
        }

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
        if (closed) {
            cacheTransactional.clear();
            throw new JdbcSessionException("The JdbcSession " + this + " is closed.but it need commit data.");
        }
        logger.debug(cacheTransactional.toString());
        cacheTransactional.commit();
        try {
            transaction.commit();
            logger.debug(this + " is commit");
        } catch (SQLException e) {
            logger.error(e.getMessage() + "\tt\n" + e.getCause());
        }
    }

    @Override
    public void rollback() {
        if (!closed) {
            cacheTransactional.rollback();
            try {
                transaction.rollback();
                logger.debug(this + " is rollback");
            } catch (SQLException e) {
                logger.error(e.getMessage() + "\tt\n" + e.getCause());
            }
        }
    }

}
