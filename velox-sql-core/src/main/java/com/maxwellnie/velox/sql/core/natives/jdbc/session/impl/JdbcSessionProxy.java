package com.maxwellnie.velox.sql.core.natives.jdbc.session.impl;

import com.maxwellnie.velox.sql.core.cache.transactional.CacheTransactional;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSession;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSessionFactory;
import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.Transaction;
import com.maxwellnie.velox.sql.core.natives.task.TaskQueue;


/**
 * JdbcSession代理，阻止了提交和回滚
 *
 * @author Maxwell Nie
 */
public class JdbcSessionProxy implements JdbcSession {
    private final JdbcSession jdbcSession;
    private final JdbcSessionFactory jdbcSessionFactory;

    public JdbcSessionProxy(JdbcSession jdbcSession, JdbcSessionFactory jdbcSessionFactory) {
        this.jdbcSession = jdbcSession;
        this.jdbcSessionFactory = jdbcSessionFactory;
    }

    @Override
    public boolean getAutoCommit() {
        return jdbcSession.getAutoCommit();
    }

    @Override
    public void setAutoCommit(boolean flag) {
        jdbcSession.setAutoCommit(flag);
    }

    @Override
    public void close() {
        jdbcSession.close();
    }

    @Override
    public void close(boolean commit) {
        jdbcSession.close(commit);
    }

    @Override
    public void commit() {

    }

    @Override
    public void rollback() {

    }

    @Override
    public Transaction getTransaction() {
        return jdbcSession.getTransaction();
    }

    @Override
    public CacheTransactional getDirtyManager() {
        return jdbcSession.getDirtyManager();
    }

    @Override
    public boolean isClosed() {
        return jdbcSession.isClosed();
    }

    @Override
    public TaskQueue getTaskQueue() {
        return jdbcSession.getTaskQueue();
    }

    public JdbcSession getTarget() {
        return jdbcSession;
    }

    public JdbcSessionFactory getJdbcSessionFactory() {
        return jdbcSessionFactory;
    }
}
