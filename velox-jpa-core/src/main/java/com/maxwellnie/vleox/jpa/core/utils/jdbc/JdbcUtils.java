package com.maxwellnie.vleox.jpa.core.utils.jdbc;

import com.maxwellnie.vleox.jpa.core.jdbc.sql.SqlStatement;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Jdbc Api相关工具
 *
 * @author Maxwell Nie
 */
public class JdbcUtils {
    /**
     * 设置param到PrepareStatement
     *
     * @param sqlStatement
     * @param preparedStatement
     * @throws SQLException
     */
    public static void setParam(SqlStatement sqlStatement, PreparedStatement preparedStatement) throws SQLException {
        List<Object> params = sqlStatement.getValues();
        for (int i = 1; i <= params.size(); i++)
            preparedStatement.setObject(i, params.get(i - 1));
    }

    /**
     * 设置param到PrepareStatement
     *
     * @param objects
     * @param preparedStatement
     * @throws SQLException
     */
    public static void setParam(List<Object> objects, PreparedStatement preparedStatement) throws SQLException {
        for (int i = 1; i <= objects.size(); i++)
            preparedStatement.setObject(i, objects.get(i - 1));
    }
}
