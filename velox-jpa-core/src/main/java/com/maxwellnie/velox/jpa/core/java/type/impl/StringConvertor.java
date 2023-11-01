package com.maxwellnie.velox.jpa.core.java.type.impl;

import com.maxwellnie.velox.jpa.core.java.type.TypeConvertor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public class StringConvertor implements TypeConvertor<String> {
    @Override
    public String convert(ResultSet resultSet, String column) throws SQLException {
        return resultSet.getString(column);
    }

    @Override
    public String convert(ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getString(columnIndex);
    }
}
