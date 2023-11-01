package com.maxwellnie.velox.jpa.core.java.type.impl;

import com.maxwellnie.velox.jpa.core.java.type.TypeConvertor;

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
}
