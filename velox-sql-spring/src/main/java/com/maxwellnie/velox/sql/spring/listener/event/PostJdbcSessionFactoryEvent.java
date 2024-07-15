package com.maxwellnie.velox.sql.spring.listener.event;

import com.maxwellnie.velox.sql.core.natives.jdbc.context.Context;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSessionFactory;

import java.util.Arrays;

/**
 * @author Maxwell Nie
 */
public class PostJdbcSessionFactoryEvent extends SupportEvent {
    protected Context context;
    protected JdbcSessionFactory jdbcSessionFactory;

    public PostJdbcSessionFactoryEvent(Context context, JdbcSessionFactory jdbcSessionFactory) {
        super(Arrays.asList(context, jdbcSessionFactory));
        this.context = context;
        this.jdbcSessionFactory = jdbcSessionFactory;
    }

    public Context getEnvironment() {
        return context;
    }

    public JdbcSessionFactory getJdbcSessionFactory() {
        return jdbcSessionFactory;
    }
}
