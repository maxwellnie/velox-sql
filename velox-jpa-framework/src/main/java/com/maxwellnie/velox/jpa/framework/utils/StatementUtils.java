package com.maxwellnie.velox.jpa.framework.utils;

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
        for (int i = 0; i < objects.size(); i++)
            preparedStatement.setObject(i + 1, objects.get(i));
    }
}
