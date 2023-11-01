package com.maxwellnie.velox.jpa.core.java.type.impl;

import com.maxwellnie.velox.jpa.core.java.type.TypeConvertor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public class LongConvertor implements TypeConvertor<Long> {
    @Override
    public Long convert(ResultSet resultSet, String column) throws SQLException {
        return resultSet.getLong(column);
    }

    @Override
    public Long convert(ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getLong(columnIndex);
    }
}
