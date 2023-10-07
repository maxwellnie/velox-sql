package com.maxwellnie.vleox.jpa.core.jdbc.context;

import com.maxwellnie.vleox.jpa.core.cahce.dirty.CacheDirtyManager;
import com.maxwellnie.vleox.jpa.core.jdbc.transaction.Transaction;
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CacheDirtyManager getDirtyManager() {
        return cacheDirtyManager;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    @Override
    public void commit() {
        cacheDirtyManager.commit();
        try {
            transaction.commit();
            logger.debug(this + " is commit");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rollback() {
        cacheDirtyManager.rollback();
        try {
            transaction.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
