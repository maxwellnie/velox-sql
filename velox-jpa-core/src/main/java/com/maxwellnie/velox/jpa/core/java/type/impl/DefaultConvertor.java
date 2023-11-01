package com.maxwellnie.velox.jpa.core.java.type.impl;

import com.maxwellnie.velox.jpa.core.java.type.TypeConvertor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public class DefaultConvertor implements TypeConvertor<Object> {
    @Override
    public Object convert(ResultSet resultSet, String column) throws SQLException {
        return resultSet.getObject(column);
    }

    @Override
    public Object convert(ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getObject(columnIndex);
    }
}
