package com.maxwellnie.velox.sql.core.natives.type.convertor.impl;

import com.maxwellnie.velox.sql.core.natives.exception.MethodNotSupportException;
import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public class NoTypeConvertor implements TypeConvertor<Object> {
    @Override
    public Object convert(ResultSet resultSet, String column) throws SQLException {
        throw new MethodNotSupportException("Is no type convertor,The method is not supported");
    }

    @Override
    public Object convert(ResultSet resultSet, int columnIndex) throws SQLException {
        throw new MethodNotSupportException("Is no type convertor,The method is not supported");
    }

    @Override
    public void addParam(PreparedStatement preparedStatement, int index, Object param) throws SQLException {
        throw new MethodNotSupportException("Is no type convertor,The method is not supported");
    }

    @Override
    public Object getEmpty() {
        throw new MethodNotSupportException("Is no type convertor,The method is not supported");
    }
}
