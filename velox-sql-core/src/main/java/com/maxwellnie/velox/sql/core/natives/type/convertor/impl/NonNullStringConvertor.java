package com.maxwellnie.velox.sql.core.natives.type.convertor.impl;

import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;
import com.maxwellnie.velox.sql.core.utils.java.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Maxwell Nie
 */
public class NonNullStringConvertor implements TypeConvertor<String> {
    @Override
    public String convert(ResultSet resultSet, String column) throws SQLException {
        return StringUtils.getNonNullStr(resultSet.getString(column));
    }

    @Override
    public String convert(ResultSet resultSet, int columnIndex) throws SQLException {
        return StringUtils.getNonNullStr(resultSet.getString(columnIndex));
    }

    @Override
    public void addParam(PreparedStatement preparedStatement, int index, Object param) throws SQLException {
        if (param == null)
            preparedStatement.setString(index, "");
        else
            preparedStatement.setString(index, (String) param);
    }

    @Override
    public String getEmpty() {
        return "";
    }
}
