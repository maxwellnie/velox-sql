package com.maxwellnie.velox.sql.core.natives.type.convertor.impl;

import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;

import java.sql.PreparedStatement;
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

    @Override
    public void addParam(PreparedStatement preparedStatement, int index, Object param) throws SQLException {
        preparedStatement.setObject(index, param);
    }

    @Override
    public Object getEmpty() {
        return null;
    }
}
