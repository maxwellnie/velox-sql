package com.maxwellnie.velox.sql.core.natives.jdbc.session;

import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.impl.jdbc.Connections;

import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public interface DataSourceAndConnectionHolder {
    Connections.DataSourceAndConnection getDataSourceAndConnection() throws SQLException;
}
