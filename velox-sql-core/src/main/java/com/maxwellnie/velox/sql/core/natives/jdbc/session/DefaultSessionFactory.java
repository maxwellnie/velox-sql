package com.maxwellnie.velox.sql.core.natives.jdbc.session;


import com.maxwellnie.velox.sql.core.natives.jdbc.context.Context;

/**
 * JdbcContext工厂，生产出一个JdbcContext实例
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
