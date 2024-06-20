package com.maxwellnie.velox.sql.core.natives.jdbc.session;

import com.maxwellnie.velox.sql.core.natives.jdbc.context.Context;
import com.maxwellnie.velox.sql.core.natives.resource.ResourceHolder;

/**
 * @author Maxwell Nie
 */
public interface JdbcSessionFactory extends ResourceHolder<Context> {
    JdbcSession produce();

    JdbcSession produce(boolean autoCommit);
}
