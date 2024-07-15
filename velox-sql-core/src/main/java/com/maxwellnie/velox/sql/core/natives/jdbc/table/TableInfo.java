package com.maxwellnie.velox.sql.core.natives.jdbc.table;

import com.maxwellnie.velox.sql.core.meta.MetaData;
import com.maxwellnie.velox.sql.core.natives.jdbc.mapping.ReturnTypeMapping;
import com.maxwellnie.velox.sql.core.natives.jdbc.resultset.parser.ResultSetParser;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.column.PrimaryInfo;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.join.JoinInfo;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 表信息
 *
 * @author Maxwell Nie
 */
public class TableInfo {
    /**
     * 其他配置
     */
    private final MetaData otherInfo = MetaData.ofEmpty();
    /**
     * 返回类型映射
     */
    private final Map<String, ReturnTypeMapping> returnTypeMappingMap = new HashMap<>();
    /**
     * 结果集解析器
     */
    private final ResultSetParser resultSetParser = new ResultSetParser();
    /**
     * 表名
     */
    private String tableName;
    /**
     * 数据源名称
     */
    private String dataSourceName;
    /**
     * 被映射实体
     */
    private Class<?> mappedClazz;
    /**
     * 列信息
     */
    private Map<String, ColumnInfo> columnMappedMap = Collections.synchronizedMap(new LinkedHashMap<>());
    /**
     * 主键信息
     */
    private PrimaryInfo pkColumn;
    /**
     * 从数据库中一次性获取的记录数量，默认为0，即一次性获取全部数据
     */
    private int fetchSize = 0;
    /**
     * 关联信息
     */
    private List<JoinInfo> joinInfos = Collections.synchronizedList(new LinkedList<>());

    public TableInfo() {
    }

    public TableInfo(String tableName, Class<?> mappedClazz, Map<String, ColumnInfo> columnMappedMap, PrimaryInfo pkColumn) {
        this.tableName = tableName;
        this.mappedClazz = mappedClazz;
        this.columnMappedMap = columnMappedMap;
        this.pkColumn = pkColumn;
    }

    public TableInfo(String tableName, Class<?> mappedClazz, Map<String, ColumnInfo> columnMappedMap, PrimaryInfo pkColumn, int fetchSize, List<JoinInfo> joinInfos) {
        this.tableName = tableName;
        this.mappedClazz = mappedClazz;
        this.columnMappedMap = columnMappedMap;
        this.pkColumn = pkColumn;
        this.fetchSize = fetchSize;
        this.joinInfos = joinInfos;
    }

    public Map<String, ReturnTypeMapping> getReturnTypeMappingMap() {
        return returnTypeMappingMap;
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

    public boolean hasPk() {
        return this.pkColumn != null;
    }

    public void putColumnInfo(String fieldName, ColumnInfo columnInfo) {
        this.columnMappedMap.put(fieldName, columnInfo);
    }

    public List<JoinInfo> getJoinInfos() {
        return joinInfos;
    }

    public void setJoinInfos(List<JoinInfo> joinInfos) {
        this.joinInfos = joinInfos;
    }

    public MetaData getOtherInfo() {
        return this.otherInfo;
    }

    public ColumnInfo getColumnInfo(String fieldName) {
        return this.columnMappedMap.get(fieldName);
    }

    public ColumnInfo getColumnInfo(Field field) {
        return getColumnInfo(field.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableInfo tableInfo = (TableInfo) o;
        return fetchSize == tableInfo.fetchSize && Objects.equals(tableName, tableInfo.tableName) && Objects.equals(mappedClazz, tableInfo.mappedClazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, mappedClazz, fetchSize);
    }

    public ReturnTypeMapping getReturnTypeMapping(String key) {
        return returnTypeMappingMap.get(key);
    }

    public void registerReturnTypeMapping(String key, ReturnTypeMapping returnTypeMapping) {
        returnTypeMappingMap.put(key, returnTypeMapping);
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public ResultSetParser getResultSetParser() {
        return resultSetParser;
    }
}
