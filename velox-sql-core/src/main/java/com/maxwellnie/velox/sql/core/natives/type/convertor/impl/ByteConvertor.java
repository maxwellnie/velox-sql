package com.maxwellnie.velox.sql.core.natives.type.convertor.impl;

import com.maxwellnie.velox.sql.core.natives.exception.TypeConvertException;
import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public class ByteConvertor implements TypeConvertor<Byte> {
    @Override
    public Byte convert(ResultSet resultSet, String column) throws SQLException {
        return resultSet.getByte(column);
    }

    @Override
    public Byte convert(ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getByte(columnIndex);
    }

    @Override
    public void addParam(PreparedStatement preparedStatement, int index, Object param) throws SQLException {
        if (param == null)
            preparedStatement.setByte(index, (byte) 0);
        else if (param instanceof String)
            preparedStatement.setByte(index, Byte.parseByte((String) param));
        else if (param instanceof Number)
            preparedStatement.setByte(index, ((Number) param).byteValue());
        else
            throw new TypeConvertException("The param [" + param + "] is not convert to java.util.Byte");
    }

    @Override
    public Byte getEmpty() {
        return 0;
    }
}
