package com.maxwellnie.velox.jpa.core.jdbc.table.column;

import com.maxwellnie.velox.jpa.core.java.type.TypeConvertor;

import java.lang.reflect.Field;

/**
 * 主键信息
 *
 * @author Maxwell Nie
 */
public class PrimaryInfo extends ColumnInfo {
    private String fieldName;
    /**
     * 策略名
     */
    private String strategyName;

    public PrimaryInfo(String columnName, Field columnMappedField, TypeConvertor<?> convertor, String fieldName, String strategyName) {
        super(columnName, columnMappedField, convertor);
        this.fieldName = fieldName;
        this.strategyName = strategyName;
    }

    public PrimaryInfo() {
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }


    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }

    @Override
    public String toString() {
        return "PrimaryInfo{" +
                "fieldName='" + fieldName + '\'' +
                ", strategyName='" + strategyName + '\'' +
                '}';
    }
}
