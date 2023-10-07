package com.maxwellnie.vleox.jpa.core.jdbc.context;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public interface JdbcConnectionUser {
    Connection getConnection() throws SQLException;
}
