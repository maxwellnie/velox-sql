package com.maxwellnie.velox.sql.core.natives.jdbc.session.impl;


import com.maxwellnie.velox.sql.core.natives.jdbc.context.Context;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSession;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSessionFactory;

/**
 * JdbcSession工厂，生产出一个JdbcSession实例
 *
 * @author Maxwell Nie
 */
public class DefaultSessionFactory implements JdbcSessionFactory {
    private final Context context;

    public DefaultSessionFactory(Context context) {
        this.context = context;
    }

    @Override
    public JdbcSession produce() {
        return produce(false);
    }

    @Override
    public JdbcSession produce(boolean autoCommit) {
        return new DefaultJdbcSession(
                context.getTransactionFactory().
                        produce(autoCommit, context.getLevel()), autoCommit, context.getTaskQueue());
    }

    @Override
    public Context getHolderObject() {
        return this.context;
    }
}
