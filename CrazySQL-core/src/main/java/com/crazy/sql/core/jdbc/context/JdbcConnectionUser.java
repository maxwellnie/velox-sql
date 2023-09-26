package com.crazy.sql.core.jdbc.context;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Akiba no ichiichiyoha
 */
public interface JdbcConnectionUser {
    Connection getConnection() throws SQLException;
}
