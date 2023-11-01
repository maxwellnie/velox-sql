package com.maxwellnie.velox.jpa.core.java.type.impl;

import com.maxwellnie.velox.jpa.core.java.type.TypeConvertor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public class FloatConvertor implements TypeConvertor<Float> {
    @Override
    public Float convert(ResultSet resultSet, String column) throws SQLException {
        return resultSet.getFloat(column);
    }

    @Override
    public Float convert(ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getFloat(columnIndex);
    }
}
