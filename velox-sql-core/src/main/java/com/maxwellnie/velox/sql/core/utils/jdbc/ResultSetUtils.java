package com.maxwellnie.velox.sql.core.utils.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * ResultSet工具类
 *
 * @author Maxwell Nie, Akiba no ichiichiyoha
 */
public class ResultSetUtils {
    private static final Logger logger = LoggerFactory.getLogger(ResultSetUtils.class);

    /**
     * 获取自增主键值
     *
     * @param resultSet
     * @return
     * @throws SQLException
     */
    public static Object[] getAutoIncrementKey(ResultSet resultSet) throws SQLException {
        if (resultSet == null || resultSet.wasNull())
            return new Object[0];
        else {
            List<Integer> list = new LinkedList<>();
            while (resultSet.next())
                list.add(resultSet.getInt(1));
            resultSet.close();
            return list.toArray();
        }
    }
}
