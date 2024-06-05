package com.maxwellnie.velox.sql.core.natives.jdbc.table.column;

import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;
import com.maxwellnie.velox.sql.core.utils.reflect.MetaField;

/**
 * 主键信息
 *
 * @author Maxwell Nie
 */
public class PrimaryInfo extends ColumnInfo {
    /**
     * 策略名
     */
    private String strategyName;

    public PrimaryInfo(String columnName, MetaField columnMappedField, TypeConvertor<?> typeConvertor, String strategyName) {
        super(columnName, columnMappedField, typeConvertor);
        this.strategyName = strategyName;
    }
    public PrimaryInfo() {
    }

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }
}
