package com.maxwellnie.velox.sql.core.natives.type.convertor.impl;

import com.maxwellnie.velox.sql.core.natives.exception.TypeConvertException;
import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;

import java.sql.PreparedStatement;
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

    @Override
    public void addParam(PreparedStatement preparedStatement, int index, Object param) throws SQLException {
        if (param == null)
            preparedStatement.setBoolean(index, false);
        else if (param instanceof String)
            preparedStatement.setBoolean(index, Boolean.parseBoolean((String) param));
        else if (param instanceof Number)
            preparedStatement.setBoolean(index, ((Number) param).doubleValue() != 0);
        else
            throw new TypeConvertException("The param [" + param + "] is not convert to java.util.Integer");
    }

    @Override
    public Boolean getEmpty() {
        return Boolean.FALSE;
    }
}
