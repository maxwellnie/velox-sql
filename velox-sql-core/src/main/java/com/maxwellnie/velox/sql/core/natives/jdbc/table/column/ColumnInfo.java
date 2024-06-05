package com.maxwellnie.velox.sql.core.natives.jdbc.table.column;

import com.maxwellnie.velox.sql.core.natives.dao.SqlDecorator;
import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;
import com.maxwellnie.velox.sql.core.utils.reflect.MetaField;

/**
 * 列信息
 *
 * @author Maxwell Nie
 */
public class ColumnInfo implements Cloneable{
    /**
     * 列名
     */
    private String columnName;
    /**
     * 列映射属性
     */
    private MetaField columnMappedField;
    private TypeConvertor<?> typeConvertor;
    public ColumnInfo() {
    }

    public ColumnInfo(String columnName, MetaField columnMappedField, TypeConvertor<?> typeConvertor) {
        this.columnName = columnName;
        this.columnMappedField = columnMappedField;
        this.typeConvertor = typeConvertor;
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

    public MetaField getColumnMappedField() {
        return columnMappedField;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ColumnInfo columnInfo = (ColumnInfo) super.clone();
        columnInfo.columnName = this.columnName;
        columnInfo.typeConvertor = this.typeConvertor;
        columnInfo.columnMappedField = this.columnMappedField;
        return columnInfo;
    }

    public void setColumnMappedField(MetaField columnMappedField) {
        this.columnMappedField = columnMappedField;
    }
    public static class ColumnInfoBuilder{
        private String fieldName;
        private String newColumnName;
        private ColumnInfoBuilder previous;
        private ColumnInfoBuilder next;
        private SqlDecorator<?> sqlDecorator;

        public ColumnInfoBuilder(ColumnInfoBuilder previous, SqlDecorator<?> sqlDecorator) {
            this.previous = previous;
            this.sqlDecorator = sqlDecorator;
        }

        public ColumnInfoBuilder columnInfo(String fieldName, String newColumnName){
            this.fieldName = fieldName;
            this.newColumnName = newColumnName;
            return this;
        }
        public ColumnInfoBuilder next(){
            this.next = new ColumnInfoBuilder(this, this.sqlDecorator);
            return next;
        }
        public SqlDecorator<?> build() {
            return sqlDecorator;
        }
    }
}
