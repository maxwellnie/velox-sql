package com.maxwellnie.velox.sql.core.natives.type.convertor.impl;

import com.maxwellnie.velox.sql.core.natives.exception.TypeConvertException;
import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public class BigDecimalConvertor implements TypeConvertor<BigDecimal> {
    @Override
    public BigDecimal convert(ResultSet resultSet, String column) throws SQLException {
        BigDecimal value = resultSet.getBigDecimal(column);
        if (value == null)
            return new BigDecimal("0");
        else
            return value;
    }

    @Override
    public BigDecimal convert(ResultSet resultSet, int columnIndex) throws SQLException {
        BigDecimal value = resultSet.getBigDecimal(columnIndex);
        if (value == null)
            return new BigDecimal("0");
        else
            return value;
    }

    @Override
    public void addParam(PreparedStatement preparedStatement, int index, Object param) throws SQLException {
        if (param == null)
            preparedStatement.setBigDecimal(index, null);
        else if (param instanceof String)
            preparedStatement.setBigDecimal(index, new BigDecimal((String) param));
        else if (param instanceof Number)
            preparedStatement.setBigDecimal(index, new BigDecimal(param.toString()));
        else
            throw new TypeConvertException("The param [" + param + "] is not convert to java.math.BigDecimal");
    }

    @Override
    public BigDecimal getEmpty() {
        return new BigDecimal("0");
    }
}
