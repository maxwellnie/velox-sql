package com.maxwellnie.velox.jpa.core.java.type.impl;

import com.maxwellnie.velox.jpa.core.java.type.TypeConvertor;

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
}
