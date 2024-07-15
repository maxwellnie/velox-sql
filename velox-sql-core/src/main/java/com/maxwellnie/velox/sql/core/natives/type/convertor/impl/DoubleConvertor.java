package com.maxwellnie.velox.sql.core.natives.type.convertor.impl;

import com.maxwellnie.velox.sql.core.natives.exception.TypeConvertException;
import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;

import java.sql.PreparedStatement;
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

    @Override
    public void addParam(PreparedStatement preparedStatement, int index, Object param) throws SQLException {
        if (param == null)
            preparedStatement.setDouble(index, 0d);
        else if (param instanceof String)
            preparedStatement.setDouble(index, Double.parseDouble((String) param));
        else if (param instanceof Number)
            preparedStatement.setDouble(index, ((Number) param).doubleValue());
        else
            throw new TypeConvertException("The param [" + param + "] is not convert to java.util.Double");
    }

    @Override
    public Double getEmpty() {
        return 0.0d;
    }
}
