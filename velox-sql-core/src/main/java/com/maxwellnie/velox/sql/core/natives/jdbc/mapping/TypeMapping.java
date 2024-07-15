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

    TypeMapping primaryKeyPropertyMapping;
    ;

    boolean isJoinedFlag;
    boolean isCollection;

    public TypeMapping() {
    }

    TypeMapping(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public List<TypeMapping> getInnerTypeMapping() {
        return innerTypeMapping;
    }

    public void setInnerTypeMapping(List<TypeMapping> innerTypeMapping) {
        this.innerTypeMapping = innerTypeMapping;
    }

    public TypeMapping getParentTypeMapping() {
        return parentTypeMapping;
    }

    public void setParentTypeMapping(TypeMapping parentTypeMapping) {
        this.parentTypeMapping = parentTypeMapping;
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

    public MetaField getMetaField() {
        return metaField;
    }

    public void setMetaField(MetaField metaField) {
        this.metaField = metaField;
    }

    public boolean isNeedInstantiate() {
        return isNeedInstantiate;
    }

    public void setNeedInstantiate(boolean needInstantiate) {
        isNeedInstantiate = needInstantiate;
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
