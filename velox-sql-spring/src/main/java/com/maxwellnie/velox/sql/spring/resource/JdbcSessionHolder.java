package com.maxwellnie.velox.sql.spring.resource;

import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSession;
import org.springframework.transaction.support.ResourceHolderSupport;

/**
 * JdbcSession持有者
 *
 * @author Maxwell Nie
 */
public class JdbcSessionHolder extends ResourceHolderSupport {
    private final JdbcSession jdbcSession;

    public JdbcSessionHolder(JdbcSession jdbcSession) {
        this.jdbcSession = jdbcSession;
    }

    public JdbcSession getJdbcSession() {
        return jdbcSession;
    }
}
