package com.maxwellnie.velox.sql.core.natives.jdbc.session;

import com.maxwellnie.velox.sql.core.natives.jdbc.context.ContextHolder;

/**
 * @author Maxwell Nie
 */
public interface JdbcSessionFactory extends ContextHolder {
    JdbcSession produce();

    JdbcSession produce(boolean autoCommit);
}
