package com.maxwellnie.velox.jpa.core.jdbc.context;

import com.maxwellnie.velox.jpa.core.dao.support.env.EnvironmentUser;

/**
 * @author Maxwell Nie
 */
public interface JdbcContextFactory extends EnvironmentUser {
    JdbcContext produce();

    JdbcContext produce(boolean autoCommit);
}
