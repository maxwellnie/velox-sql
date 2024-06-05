package com.maxwellnie.velox.sql.core.natives.type.convertor.impl;

import com.maxwellnie.velox.sql.core.natives.exception.TypeConvertException;
import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;

import java.sql.*;

/**
 * @author Maxwell Nie
 */
public class TimestampConvertor implements TypeConvertor<Timestamp> {
    @Override
    public Timestamp convert(ResultSet resultSet, String column) throws SQLException {
        return resultSet.getTimestamp(column);
    }

    @Override
    public Timestamp convert(ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getTimestamp(columnIndex);
    }

    @Override
    public void addParam(PreparedStatement preparedStatement, int index, Object param) throws SQLException {
        Timestamp timestamp ;
        if (param == null)
            timestamp =null;
        else if (param instanceof Timestamp)
            timestamp = (Timestamp) param;
        else if(param instanceof Date)
            timestamp = new Timestamp(((Date)param).getTime());
        else if (param instanceof String)
            timestamp = Timestamp.valueOf((String) param);
        else if (param instanceof Long)
            timestamp = new Timestamp((Long) param);
        else
            throw new TypeConvertException("The object ["+param+"] is not convert to java.sql.TimeStamp");
        preparedStatement.setTimestamp(index, timestamp);
    }

    @Override
    public Timestamp getEmpty() {
        return null;
    }
}
