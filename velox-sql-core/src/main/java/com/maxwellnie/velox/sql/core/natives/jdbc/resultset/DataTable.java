package com.maxwellnie.velox.sql.core.natives.jdbc.resultset;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 数据表
 * @author Maxwell Nie
 */
public class DataTable {
    /**
     * 数据库结果集
     */
    private final ResultSet resultSet;
    /**
     * 数据表列名
     */
    private final List<String> columns;
    /**
     * 数据表行号
     */
    private int rowIndex = 0;
    /**
     * 数据表缓存行数据限制
     */
    private final int fetchSize;
    /**
     * 数据表数据
     */
    private final Map<Integer, Map<String, Object>> data;

    public DataTable(ResultSet resultSet, int fetchSize) throws SQLException {
        this.resultSet = resultSet;
        this.columns = new ArrayList<>();
        this.fetchSize = fetchSize;
        for (int i = 1; i <= this.resultSet.getMetaData().getColumnCount();i++){
            columns.add(this.resultSet.getMetaData().getColumnName(i));
        }
        data = new LinkedHashMap<Integer, Map<String, Object>>(fetchSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Map<String, Object>> eldest) {
                return size() > DataTable.this.fetchSize;
            }
        };
    }

    public DataTable(ResultSet resultSet, List<String> columns, int fetchSize) {
        this.resultSet = resultSet;
        this.columns = columns;
        this.fetchSize = fetchSize;
        data = new LinkedHashMap<Integer, Map<String, Object>>(fetchSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Map<String, Object>> eldest) {
                return size() > DataTable.this.fetchSize;
            }
        };
    }

    /**
     * 读取一行数据
     * @return
     * @throws SQLException
     */
    public Map<String, Object> readRowDataMap() throws SQLException {
        requestNextRow();
        Map<String, Object> map = new HashMap<>();
        for (String column : columns) {
            map.put(column, resultSet.getObject(column));
        }
        data.put(rowIndex, map);
        return map;
    }
    /**
     * 读取指定行数据
     * @param index
     * @return
     * @throws SQLException
     */
    public Map<String, Object> readRowDataMap(int index) throws SQLException {
        if (data.containsKey(index)) {
            return data.get(index);
        }else if(index < rowIndex){
          throw new SQLException("Row data is out of date and inaccessible, yourIndex="+index+", currentIndex="+rowIndex);
        } else {
            Map<String, Object> map = null;
            while (this.rowIndex < index){
                map = readRowDataMap();
                data.put(rowIndex, map);
            }
            return map;
        }
    }
    /**
     * 获取当前行号
     * @return
     */
    public int getCurrentRow(){
        return rowIndex;
    }
    /**
     * 获取当前所有行数据
     * @return
     */
    public Collection<Map<String, Object>> getCurrentAllRowData(){
        return data.values();
    }
    /**
     * 读取下一行数据
     * @throws SQLException
     */
    private void requestNextRow() throws SQLException {
        if (resultSet.next()) {
            ++rowIndex;
            return;
        }
        throw new SQLException("No more rows");
    }
}
