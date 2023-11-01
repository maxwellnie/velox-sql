package com.maxwellnie.velox.jpa.core.java.type;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public interface TypeConvertor<T> {
    T convert(ResultSet resultSet, String column) throws SQLException;
    T convert(ResultSet resultSet, int columnIndex) throws SQLException;
}
