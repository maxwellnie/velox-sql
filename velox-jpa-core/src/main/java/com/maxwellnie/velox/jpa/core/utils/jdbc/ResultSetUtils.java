package com.maxwellnie.velox.jpa.core.utils.jdbc;

import com.maxwellnie.velox.jpa.core.exception.NotMappedFieldException;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.column.PrimaryInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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

    /**
     * 转换ResultSet到实体类对象
     *
     * @param resultSet
     * @param tableInfo
     * @return
     * @throws SQLException
     */
    public static List convertEntity(ResultSet resultSet, TableInfo tableInfo) throws SQLException {
        List<Object> resultList = new LinkedList<>();
        Collection<ColumnInfo> columnInfos = tableInfo.getColumnMappedMap().values();
        while (resultSet.next()) {
            Object result = null;
            try {
                result = tableInfo.getMappedClazz().getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                logger.error("The " + tableInfo.getMappedClazz().getName() + " is not instant.\t\nmessage:" + e.getMessage() + "\t\ncause:" + e.getCause());
            }
            try {
                if (tableInfo.hasPk()) {
                    PrimaryInfo primaryInfo = tableInfo.getPkColumn();
                    primaryInfo.getColumnMappedField().set(result, primaryInfo.getTypeConvertor().convert(resultSet.getObject(primaryInfo.getColumnName())));
                }
                for (ColumnInfo columnInfo : columnInfos) {
                    columnInfo.getColumnMappedField().set(result, columnInfo.getTypeConvertor().convert(resultSet.getObject(columnInfo.getColumnName())));
                }
            } catch (IllegalAccessException | IllegalArgumentException e) {
                throw new NotMappedFieldException("Your entity has a not mapped field,Please check your code!\t\n" + e.getCause());
            }
            resultList.add(result);
        }
        return resultList;
    }
}
