package com.maxwellnie.velox.sql.core.utils.jdbc;

import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class StatementUtils {
    /**
     * 设置param到PrepareStatement
     *
     * @param objects
     * @param preparedStatement
     * @throws SQLException
     */
    public static void setParam(List<Object> objects, PreparedStatement preparedStatement) throws SQLException {
        assert preparedStatement != null : "PreparedStatement must not be null.";
        for (int i = 0; i < objects.size(); i++)
            preparedStatement.setObject(i + 1, objects.get(i));
    }

    /**
     * 设置param到PrepareStatement
     *
     * @param objects
     * @param preparedStatement
     * @param typeConvertors
     * @throws SQLException
     */
    public static void setParam(List<Object> objects, PreparedStatement preparedStatement, List<TypeConvertor<?>> typeConvertors) throws SQLException {
        if (objects == null || objects.isEmpty())
            return;
        for (int i = 0; i < objects.size(); i++)
            typeConvertors.get(i).addParam(preparedStatement, i + 1, objects.get(i));
    }
}
