package com.maxwellnie.velox.jpa.core.java.type.impl;

import com.maxwellnie.velox.jpa.core.java.type.TypeConvertor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public class IntegerConvertor implements TypeConvertor<Integer> {
    @Override
    public Integer convert(ResultSet resultSet, String column) throws SQLException {
        return resultSet.getInt(column);
    }

    @Override
    public Integer convert(ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getInt(columnIndex);
    }
}
