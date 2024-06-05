package com.maxwellnie.velox.sql.core.natives.type.convertor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public interface TypeConvertor<T> {
    T convert(ResultSet resultSet, String column) throws SQLException;

    T convert(ResultSet resultSet, int columnIndex) throws SQLException;
    void addParam(PreparedStatement preparedStatement, int index, Object param) throws SQLException;
    T getEmpty();
}
