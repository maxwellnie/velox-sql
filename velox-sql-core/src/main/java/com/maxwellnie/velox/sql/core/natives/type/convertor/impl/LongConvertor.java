package com.maxwellnie.velox.sql.core.natives.type.convertor.impl;

import com.maxwellnie.velox.sql.core.natives.exception.TypeConvertException;
import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public class LongConvertor implements TypeConvertor<Long> {
    @Override
    public Long convert(ResultSet resultSet, String column) throws SQLException {
        return resultSet.getLong(column);
    }

    @Override
    public Long convert(ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getLong(columnIndex);
    }

    @Override
    public void addParam(PreparedStatement preparedStatement, int index, Object param) throws SQLException {
        if (param == null)
            preparedStatement.setLong(index, 0L);
        else if (param instanceof String)
            preparedStatement.setLong(index, Long.parseLong((String) param));
        else if (param instanceof Number)
            preparedStatement.setLong(index, ((Number) param).longValue());
        else
            throw new TypeConvertException("The param [" + param + "] is not convert to java.util.Long");
    }

    @Override
    public Long getEmpty() {
        return 0L;
    }
}
