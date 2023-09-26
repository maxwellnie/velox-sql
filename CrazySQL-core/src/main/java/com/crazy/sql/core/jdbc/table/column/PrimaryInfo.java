package com.crazy.sql.core.jdbc.table.column;

import com.crazy.sql.core.enums.PrimaryMode;
import com.crazy.sql.core.java.type.TypeConvertor;

import java.lang.reflect.Field;

/**
 * 主键信息
 * @author Akiba no ichiichiyoha
 */
public class PrimaryInfo extends ColumnInfo{
    private String fieldName;

    public PrimaryInfo(String columnName, Field columnMappedField, TypeConvertor convertor, String fieldName, PrimaryMode primaryMode, String strategyName) {
        super(columnName, columnMappedField,convertor);
        this.fieldName = fieldName;
        this.primaryMode = primaryMode;
        this.strategyName = strategyName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public PrimaryInfo() {
    }

    /**
     * 主键策略模式
     */
    private PrimaryMode primaryMode;
    /**
     * 策略名
     */
    private String strategyName;

    public PrimaryMode getPrimaryMode() {
        return primaryMode;
    }

    public void setPrimaryMode(PrimaryMode primaryMode) {
        this.primaryMode = primaryMode;
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
                "primaryMode=" + primaryMode +
                ", strategyName='" + strategyName + '\'' +
                '}';
    }
}
