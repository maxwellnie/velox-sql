package com.maxwellnie.velox.sql.core.natives.jdbc.session;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public interface ConnectionHolder {
    Connection getConnection() throws SQLException;
}
