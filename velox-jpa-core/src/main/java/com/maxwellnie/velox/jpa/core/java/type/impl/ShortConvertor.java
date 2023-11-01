package com.maxwellnie.velox.jpa.core.java.type.impl;

import com.maxwellnie.velox.jpa.core.java.type.TypeConvertor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public class ShortConvertor implements TypeConvertor<Short> {
    @Override
    public Short convert(ResultSet resultSet, String column) throws SQLException {
        return resultSet.getShort(column);
    }

    @Override
    public Short convert(ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getShort(columnIndex);
    }
}
