package com.maxwellnie.velox.sql.core.natives.type.convertor.impl;

import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public class StringConvertor implements TypeConvertor<String> {
    @Override
    public String convert(ResultSet resultSet, String column) throws SQLException {
        return resultSet.getString(column);
    }

    @Override
    public String convert(ResultSet resultSet, int columnIndex) throws SQLException {
        return resultSet.getString(columnIndex);
    }

    @Override
    public void addParam(PreparedStatement preparedStatement, int index, Object param) throws SQLException {
        if (param == null)
            preparedStatement.setString(index, null);
        else
            preparedStatement.setString(index, (String) param);
    }

    @Override
    public String getEmpty() {
        return "";
    }
}
