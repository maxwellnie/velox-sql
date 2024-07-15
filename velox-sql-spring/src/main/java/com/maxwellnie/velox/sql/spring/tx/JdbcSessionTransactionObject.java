package com.maxwellnie.velox.sql.spring.tx;

import com.maxwellnie.velox.sql.core.natives.jdbc.session.impl.JdbcSessionProxy;
import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.impl.jdbc.Connections;
import com.maxwellnie.velox.sql.spring.resource.JdbcSessionHolder;

import java.util.function.Consumer;

/**
 * @author Maxwell Nie
 */
public class JdbcSessionTransactionObject {
    private JdbcSessionHolder jdbcSessionHolder;
    private JdbcSessionProxy jdbcSessionProxy;
    private boolean newJdbcSessionHolder;
    private boolean originalAutoCommit;
    private Consumer<Connections.DataSourceAndConnection> previousConnectionConsumer;

    public JdbcSessionHolder getJdbcSessionHolder() {
        return jdbcSessionHolder;
    }

    public void setJdbcSessionHolder(JdbcSessionHolder jdbcSessionHolder) {
        this.jdbcSessionHolder = jdbcSessionHolder;
    }

    public void setJdbcSessionHolder(JdbcSessionHolder jdbcSessionHolder, boolean newJdbcSessionHolder) {
        this.jdbcSessionHolder = jdbcSessionHolder;
        this.newJdbcSessionHolder = newJdbcSessionHolder;
    }

    public boolean getNewJdbcSessionHolder() {
        return newJdbcSessionHolder;
    }

    public void setNewJdbcSessionHolder(boolean newJdbcSessionHolder) {
        this.newJdbcSessionHolder = newJdbcSessionHolder;
    }

    public JdbcSessionProxy getJdbcSessionProxy() {
        return jdbcSessionProxy;
    }

    public void setJdbcSessionProxy(JdbcSessionProxy jdbcSessionProxy) {
        this.jdbcSessionProxy = jdbcSessionProxy;
        this.originalAutoCommit = jdbcSessionProxy.getAutoCommit();
    }

    public boolean getOriginalAutoCommit() {
        return originalAutoCommit;
    }

    public void setOriginalAutoCommit(boolean originalAutoCommit) {
        this.originalAutoCommit = originalAutoCommit;
    }

    public boolean hasJdbcSessionHolder() {
        return jdbcSessionHolder != null;
    }

    public Consumer<Connections.DataSourceAndConnection> getPreviousConnectionConsumer() {
        return previousConnectionConsumer;
    }

    public void setPreviousConnectionConsumer(Consumer<Connections.DataSourceAndConnection> previousConnectionConsumer) {
        this.previousConnectionConsumer = previousConnectionConsumer;
    }
}
