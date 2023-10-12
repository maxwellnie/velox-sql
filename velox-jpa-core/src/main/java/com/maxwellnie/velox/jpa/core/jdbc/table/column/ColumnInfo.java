package com.maxwellnie.velox.jpa.core.jdbc.table.column;

import com.maxwellnie.velox.jpa.core.java.type.TypeConvertor;

import java.lang.reflect.Field;

/**
 * 列信息
 *
 * @author Maxwell Nie
 */
public class ColumnInfo {
    /**
     * 列名
     */
    private String columnName;
    /**
     * 列映射属性
     */
    private Field columnMappedField;
    private TypeConvertor<?> typeConvertor;

    public ColumnInfo(String columnName, Field columnMappedField, TypeConvertor<?> typeConvertor) {
        this.columnName = columnName;
        this.columnMappedField = columnMappedField;
        this.typeConvertor = typeConvertor;
    }

    public ColumnInfo() {
    }

    public TypeConvertor<?> getTypeConvertor() {
        return typeConvertor;
    }

    public void setTypeConvertor(TypeConvertor<?> typeConvertor) {
        this.typeConvertor = typeConvertor;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Field getColumnMappedField() {
        columnMappedField.setAccessible(true);
        return columnMappedField;
    }

    public void setColumnMappedField(Field columnMappedField) {
        this.columnMappedField = columnMappedField;
    }

    @Override
    public String toString() {
        return "ColumnInfo{" +
                "columnName='" + columnName + '\'' +
                ", columnMappedField=" + columnMappedField +
                '}';
    }
}
