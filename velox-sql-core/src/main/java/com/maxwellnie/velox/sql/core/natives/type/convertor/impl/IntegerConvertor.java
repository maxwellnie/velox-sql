package com.maxwellnie.velox.sql.core.natives.type.convertor.impl;

import com.maxwellnie.velox.sql.core.natives.exception.TypeConvertException;
import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;

import java.sql.PreparedStatement;
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

    @Override
    public void addParam(PreparedStatement preparedStatement, int index, Object param) throws SQLException {
        if (param == null)
            preparedStatement.setInt(index, 0);
        else if (param instanceof String)
            preparedStatement.setInt(index, Integer.parseInt((String) param));
        else if (param instanceof Number)
            preparedStatement.setInt(index, ((Number) param).intValue());
        else
            throw new TypeConvertException("The param [" + param + "] is not convert to java.util.Integer");
    }

    @Override
    public Integer getEmpty() {
        return 0;
    }
}
