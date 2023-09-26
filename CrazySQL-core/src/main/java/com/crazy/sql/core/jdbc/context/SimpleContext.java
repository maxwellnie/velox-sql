package com.crazy.sql.core.jdbc.context;

import com.crazy.sql.core.cahce.dirty.DirtyManager;
import com.crazy.sql.core.jdbc.transaction.Transaction;

import java.sql.SQLException;

/**
 * @author Akiba no ichiichiyoha
 */
public class SimpleContext implements JdbcContext {
    private DirtyManager dirtyManager=new DirtyManager();
    private Transaction transaction;
    private boolean autoCommit;

    public SimpleContext() {
    }

    public SimpleContext(Transaction transaction, boolean autoCommit) {
        this.transaction = transaction;
        this.autoCommit = autoCommit;
    }

    @Override
    public void setAutoCommit(boolean flag) {
        this.autoCommit=flag;
    }

    @Override
    public boolean getAutoCommit() {
        return this.autoCommit;
    }

    @Override
    public void close() {
        try {
            transaction.release();
            dirtyManager.clear();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public DirtyManager getDirtyManager() {
        return dirtyManager;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    @Override
    public void commit() {
        dirtyManager.commit();
        try {
            transaction.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rollback() {
        dirtyManager.rollback();
        try {
            transaction.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
