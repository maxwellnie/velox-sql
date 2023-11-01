package com.maxwellnie.velox.jpa.core.java.type.impl;

import com.maxwellnie.velox.jpa.core.java.type.TypeConvertor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public class DoubleConvertor implements TypeConvertor<Double> {
    @Override
    public Double convert(ResultSet resultSet, String column) throws SQLException {
        return resultSet.getDouble(column);
    }

    @Override
    public Double convert(ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getDouble(columnIndex);
    }
}
