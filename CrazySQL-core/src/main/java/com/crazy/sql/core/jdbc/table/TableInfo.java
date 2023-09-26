package com.crazy.sql.core.jdbc.table;

import com.crazy.sql.core.jdbc.table.column.PrimaryInfo;
import com.crazy.sql.core.jdbc.table.column.ColumnInfo;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 表信息
 * @author Akiba no ichiichiyoha
 */
public class TableInfo {
    /**
     * 表名
     */
    private String tableName;
    /**
     * 被映射实体
     */
    private Class<?> mappedClazz;
    /**
     * 列信息
     */
    private Map<String, ColumnInfo> columnMappedMap= Collections.synchronizedMap(new LinkedHashMap<>());
    /**
     * 主键信息
     */
    private PrimaryInfo pkColumn;
    /**
     *从数据库中一次性获取的记录数量，默认为0，即一次性获取全部数据
     */
    private int fetchSize=0;

    public TableInfo() {
    }

    public TableInfo(String tableName, Class<?> mappedClazz, Map<String, ColumnInfo> columnMappedMap, PrimaryInfo pkColumn) {
        this.tableName = tableName;
        this.mappedClazz = mappedClazz;
        this.columnMappedMap = columnMappedMap;
        this.pkColumn = pkColumn;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Class<?> getMappedClazz() {
        return mappedClazz;
    }

    public void setMappedClazz(Class<?> mappedClazz) {
        this.mappedClazz = mappedClazz;
    }

    public Map<String, ColumnInfo> getColumnMappedMap() {
        return columnMappedMap;
    }

    public void setColumnMappedMap(Map<String, ColumnInfo> columnMappedMap) {
        this.columnMappedMap = columnMappedMap;
    }

    public PrimaryInfo getPkColumn() {
        return pkColumn;
    }

    public void setPkColumn(PrimaryInfo pkColumn) {
        this.pkColumn = pkColumn;
    }

    public boolean hasPk(){
        return this.pkColumn!=null;
    }
    public void putColumnInfo(String fieldName,ColumnInfo columnInfo){
        this.columnMappedMap.put(fieldName,columnInfo);
    }

    @Override
    public String toString() {
        return "TableInfo{" +
                "tableName='" + tableName + '\'' +
                ", mappedClazz=" + mappedClazz +
                ", columnMappedMap=" + columnMappedMap +
                ", pkColumn=" + pkColumn +
                ", fetchSize=" + fetchSize +
                '}';
    }
}
