package com.maxwellnie.velox.sql.core.natives.type.convertor.impl;

import com.maxwellnie.velox.sql.core.natives.exception.TypeConvertException;
import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;

import java.sql.PreparedStatement;
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

    @Override
    public void addParam(PreparedStatement preparedStatement, int index, Object param) throws SQLException {
        if (param == null)
            preparedStatement.setShort(index, (short) 0);
        else if (param instanceof String)
            preparedStatement.setShort(index, Short.parseShort((String) param));
        else if (param instanceof Number)
            preparedStatement.setShort(index, ((Number) param).shortValue());
        else
            throw new TypeConvertException("The param [" + param + "] is not convert to java.util.Short");
    }

    @Override
    public Short getEmpty() {
        return 0;
    }
}
