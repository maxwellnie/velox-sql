package com.maxwellnie.velox.sql.core.natives.type.convertor.impl;

import com.maxwellnie.velox.sql.core.natives.exception.TypeConvertException;
import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;

import java.sql.PreparedStatement;
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

    @Override
    public void addParam(PreparedStatement preparedStatement, int index, Object param) throws SQLException {
        if (param == null)
            preparedStatement.setFloat(index, 0f);
        else if (param instanceof String)
            preparedStatement.setFloat(index, Float.parseFloat((String) param));
        else if (param instanceof Number)
            preparedStatement.setFloat(index, ((Number) param).floatValue());
        else
            throw new TypeConvertException("The param [" + param + "] is not convert to java.util.Float");
    }

    @Override
    public Float getEmpty() {
        return 0.0f;
    }
}
