package com.maxwellnie.vleox.jpa.core.jdbc.context;

import com.maxwellnie.vleox.jpa.core.dao.support.env.EnvironmentUser;

/**
 * @author Maxwell Nie
 */
public interface JdbcContextFactory extends EnvironmentUser {
    JdbcContext produce();

    JdbcContext produce(boolean autoCommit);
}
