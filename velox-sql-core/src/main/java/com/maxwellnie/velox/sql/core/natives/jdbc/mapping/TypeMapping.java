package com.maxwellnie.velox.sql.core.natives.jdbc.mapping;

import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;
import com.maxwellnie.velox.sql.core.utils.reflect.MetaField;

import java.util.List;

/**
 * 类型映射
 */
public class TypeMapping {
    public static final TypeMapping READ_ROW = new TypeMapping(TypeMapping.class);
    public static final TypeMapping BATCH_ROW = new TypeMapping(TypeMapping.class);
    Class<?> type;
    List<TypeMapping> innerTypeMapping;
    TypeMapping parentTypeMapping;
    TypeConvertor<?> typeConvertor;
    String columnName;
    MetaField metaField;
    boolean isNeedInstantiate = false;

    TypeMapping primaryKeyPropertyMapping;;

    boolean isJoinedFlag;
    boolean isCollection;

    public TypeMapping() {
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public void setInnerTypeMapping(List<TypeMapping> innerTypeMapping) {
        this.innerTypeMapping = innerTypeMapping;
    }

    public void setParentTypeMapping(TypeMapping parentTypeMapping) {
        this.parentTypeMapping = parentTypeMapping;
    }

    public void setTypeConvertor(TypeConvertor<?> typeConvertor) {
        this.typeConvertor = typeConvertor;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setMetaField(MetaField metaField) {
        this.metaField = metaField;
    }

    public void setNeedInstantiate(boolean needInstantiate) {
        isNeedInstantiate = needInstantiate;
    }

    public Class<?> getType() {
        return type;
    }

    public List<TypeMapping> getInnerTypeMapping() {
        return innerTypeMapping;
    }

    public TypeMapping getParentTypeMapping() {
        return parentTypeMapping;
    }

    public TypeConvertor<?> getTypeConvertor() {
        return typeConvertor;
    }

    public String getColumnName() {
        return columnName;
    }

    public MetaField getMetaField() {
        return metaField;
    }

    public boolean isNeedInstantiate() {
        return isNeedInstantiate;
    }

    TypeMapping(Class<?> type) {
        this.type = type;
    }

    public TypeMapping getPrimaryKeyPropertyMapping() {
        return primaryKeyPropertyMapping;
    }

    public void setPrimaryKeyPropertyMapping(TypeMapping primaryKeyPropertyMapping) {
        this.primaryKeyPropertyMapping = primaryKeyPropertyMapping;
    }

    public boolean isJoinedFlag() {
        return isJoinedFlag;
    }

    public void setJoinedFlag(boolean joinedFlag) {
        isJoinedFlag = joinedFlag;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }
}
