package com.maxwellnie.velox.jpa.core.jdbc.context;

import com.maxwellnie.velox.jpa.core.cahce.dirty.CacheDirtyManager;
import com.maxwellnie.velox.jpa.core.exception.JdbcContextException;
import com.maxwellnie.velox.jpa.core.jdbc.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public class SimpleContext implements JdbcContext {
    private static final Logger logger = LoggerFactory.getLogger(SimpleContext.class);
    private CacheDirtyManager cacheDirtyManager = new CacheDirtyManager();
    private Transaction transaction;
    private boolean autoCommit;
    private boolean closed = false;

    public SimpleContext() {
    }

    public SimpleContext(Transaction transaction, boolean autoCommit) {
        this.transaction = transaction;
        this.autoCommit = autoCommit;
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
    public void close() {
        try {
            transaction.release();
            cacheDirtyManager.clear();
            this.closed = true;
        } catch (SQLException e) {
            logger.error(e.getMessage() + "\tt\n" + e.getCause());
        }
    }

    public CacheDirtyManager getDirtyManager() {
        return cacheDirtyManager;
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
            cacheDirtyManager.clear();
            throw new JdbcContextException("The JdbcContext " + this + " is closed.but it need commit data.");
        }
        logger.debug(cacheDirtyManager.toString());
        cacheDirtyManager.commit();
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
            cacheDirtyManager.rollback();
            try {
                transaction.rollback();
                logger.debug(this + " is rollback");
            } catch (SQLException e) {
                logger.error(e.getMessage() + "\tt\n" + e.getCause());
            }
        }
    }
}
