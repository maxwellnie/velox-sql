package com.maxwellnie.velox.sql.core.natives.type.convertor.impl;

import com.maxwellnie.velox.sql.core.natives.exception.TypeConvertException;
import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Maxwell Nie
 */
public class DateConvertor implements TypeConvertor<Date> {
    @Override
    public Date convert(ResultSet resultSet, String column) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp(column);
        if (timestamp != null)
            return new Date(timestamp.getTime());
        else
            return null;
    }

    @Override
    public Date convert(ResultSet resultSet, int columnIndex) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp(columnIndex);
        if (timestamp != null)
            return new Date(timestamp.getTime());
        else
            return null;
    }

    @Override
    public void addParam(PreparedStatement preparedStatement, int index, Object param) throws SQLException {
        java.sql.Date date;
        if (param == null)
            date = null;
        else if (param instanceof java.sql.Date)
            date = (java.sql.Date) param;
        else if (param instanceof Date)
            date = new java.sql.Date(((Date) param).getTime());
        else if (param instanceof String)
            date = java.sql.Date.valueOf((String) param);
        else if (param instanceof Long)
            date = new java.sql.Date((Long) param);
        else
            throw new TypeConvertException("The object [" + param + "] is not convert to java.sql.Date");
        preparedStatement.setDate(index, date);
    }

    @Override
    public Date getEmpty() {
        return null;
    }
}
