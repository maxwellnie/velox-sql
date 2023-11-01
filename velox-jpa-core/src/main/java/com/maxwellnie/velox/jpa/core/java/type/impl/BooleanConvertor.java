package com.maxwellnie.velox.jpa.core.java.type.impl;

import com.maxwellnie.velox.jpa.core.java.type.TypeConvertor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public class BooleanConvertor implements TypeConvertor<Boolean> {
    @Override
    public Boolean convert(ResultSet resultSet, String column) throws SQLException {
        return resultSet.getBoolean(column);
    }

    @Override
    public Boolean convert(ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getBoolean(columnIndex);
    }
}
