package com.maxwellnie.velox.sql.core.natives.jdbc.mapping;

import com.maxwellnie.velox.sql.core.natives.type.convertor.ConvertorManager;
import com.maxwellnie.velox.sql.core.meta.MetaData;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfo;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.column.PrimaryInfo;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.join.JoinInfo;
import com.maxwellnie.velox.sql.core.utils.java.TypeUtils;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfoManager;

import java.util.*;

import static com.maxwellnie.velox.sql.core.natives.type.convertor.ConvertorManager.DEFAULT_CONVERTOR;

/**
 * 类型解析器，用于解析返回值类型
 * @author Maxwell Nie
 */
public class DefaultTypeParser implements TypeParser{
    @Override
    public ReturnTypeMapping parse(Class<?> returnType, Class<?> entityClass) {
        return parse(returnType, TableInfoManager.getTableInfo(entityClass));
    }
    @Override
    public ReturnTypeMapping parse(Class<?> returnType, TableInfo tableInfo) {
        ReturnTypeMapping returnTypeMapping = new ReturnTypeMapping();
        returnTypeMapping.setType(returnType);
        if(returnType.isArray()){
            if(returnType.equals(int[].class)){
                returnTypeMapping.setTypeMapping(TypeMapping.BATCH_ROW);
                return returnTypeMapping;
            }
            throw new UnsupportedOperationException("Array type["+returnType+"] is not supported.");
        }
        if(ConvertorManager.getConvertor(returnType) != DEFAULT_CONVERTOR){
            returnTypeMapping.setTypeMapping(TypeMapping.READ_ROW);
            return returnTypeMapping;
        }
        Class<?> finalReturnType = getAdaptableType(returnType);
        boolean isCollection = TypeUtils.isCollection(finalReturnType);
        if(isCollection){
            returnTypeMapping.setReturnManyObject(true);
        }
        if(!isCollection && finalReturnType == returnType ){
            finalReturnType = ArrayList.class;
        }
        TypeMapping rootTypeMapping = getTypeMapping(tableInfo, tableInfo.getMappedClazz(), null, returnTypeMapping);
        rootTypeMapping.setCollection(true);
        returnTypeMapping.setType(finalReturnType);
        if (!tableInfo.getJoinInfos().isEmpty())
            returnTypeMapping.setHasJoin(true);
        rootTypeMapping.setNeedInstantiate(true);
        returnTypeMapping.setTypeMapping(rootTypeMapping);
        return returnTypeMapping;
    }
    /**
     * 获取类型映射
     * @param tableInfo
     * @param finalReturnType
     * @param parentTypeMapping
     * @return
     */
    private TypeMapping getTypeMapping(TableInfo tableInfo, Class<?> finalReturnType, TypeMapping parentTypeMapping, ReturnTypeMapping returnTypeMapping) {
        TypeMapping typeMapping = new TypeMapping(finalReturnType);
        typeMapping.setParentTypeMapping(parentTypeMapping);
        typeMapping.setNeedInstantiate(true);
        List<TypeMapping> innerTypeMappings = new ArrayList<>();
        if(tableInfo.hasPk()){
            PrimaryInfo pkColumn = tableInfo.getPkColumn();
            TypeMapping pkPropertyMapping = parserPropertyMapping(typeMapping, pkColumn);
            innerTypeMappings.add(pkPropertyMapping);
        }
        for (ColumnInfo columnInfo : tableInfo.getColumnMappedMap().values()){
            TypeMapping propertyMapping = parserPropertyMapping(typeMapping, columnInfo);
            innerTypeMappings.add(propertyMapping);
        }
        returnTypeMapping.setHasJoin(parseHasJoinedChildPropertyTypeMapping(tableInfo, typeMapping, innerTypeMappings, returnTypeMapping));
        typeMapping.setInnerTypeMapping(innerTypeMappings);
        return typeMapping;
    }
    /**
     * 解析关联的属性类型映射
     * @param tableInfo
     * @param typeMapping
     * @param innerTypeMappings
     * @return
     */
    private boolean parseHasJoinedChildPropertyTypeMapping(TableInfo tableInfo, TypeMapping typeMapping, List<TypeMapping> innerTypeMappings, ReturnTypeMapping returnTypeMapping) {
        boolean hasJoined = !tableInfo.getJoinInfos().isEmpty();
        for (JoinInfo joinInfo : tableInfo.getJoinInfos()){
            TableInfo slaveTableInfo;
            if(joinInfo.isNotNested()){
                slaveTableInfo = TableInfoManager.getTableInfo(tableInfo.getMappedClazz().getName() + " - " + joinInfo.getSlaveTableName());
                for (ColumnInfo columnInfo : slaveTableInfo.getColumnMappedMap().values()){
                    TypeMapping propertyMapping = parserPropertyMapping(typeMapping, columnInfo);
                    innerTypeMappings.add(propertyMapping);
                }
                return false;
            }
            slaveTableInfo = TableInfoManager.getTableInfo(joinInfo.getSlaveTable());
            Class<?> fieldAdaptTableClass = getAdaptableType(joinInfo.getField().getType());
            boolean isCollection = TypeUtils.isCollection(fieldAdaptTableClass);
            TypeMapping slaveTypeMapping;
            if(isCollection && joinInfo.isManyToMany()){
                slaveTypeMapping = getJoinedWrapperTypeMapping(typeMapping, joinInfo, slaveTableInfo, fieldAdaptTableClass, returnTypeMapping);
            }else {
                slaveTypeMapping = getTypeMapping(slaveTableInfo, joinInfo.getSlaveTable(), typeMapping, returnTypeMapping);
            }
            if(slaveTypeMapping.isCollection()){
                MetaData fieldsMetaData = tableInfo.getOtherInfo().getProperty("fields");
                for (TypeMapping parentPropertyTypeMapping : innerTypeMappings){
                    if (parentPropertyTypeMapping.getMetaField().getField() == fieldsMetaData.getProperty(joinInfo.getMasterTableField()))
                        parentPropertyTypeMapping.setJoinedFlag(true);
                }
            }
            innerTypeMappings.add(slaveTypeMapping);
        }
        return hasJoined;
    }

    /**
     * 获取关联表的包装器的类型映射
     * @param parentTypeMapping
     * @param joinInfo
     * @param slaveTableInfo
     * @param fieldAdaptTableClass
     * @param returnTypeMapping
     * @return
     */
    private TypeMapping getJoinedWrapperTypeMapping(TypeMapping parentTypeMapping, JoinInfo joinInfo, TableInfo slaveTableInfo, Class<?> fieldAdaptTableClass, ReturnTypeMapping returnTypeMapping) {
        TypeMapping slaveTypeMapping = new TypeMapping(fieldAdaptTableClass);
        slaveTypeMapping.setParentTypeMapping(parentTypeMapping);
        slaveTypeMapping.setMetaField(joinInfo.getField());
        slaveTypeMapping.setNeedInstantiate(true);
        slaveTypeMapping.setCollection(true);
        List<TypeMapping> slaveTypeMappingInnerTypeMappings = new ArrayList<>();
        slaveTypeMappingInnerTypeMappings.add(getTypeMapping(slaveTableInfo, joinInfo.getSlaveTable(), slaveTypeMapping, returnTypeMapping));
        slaveTypeMapping.setInnerTypeMapping(slaveTypeMappingInnerTypeMappings);
        return slaveTypeMapping;
    }

    /**
     * 获取属性映射
     * @param typeMapping
     * @param columnInfo
     * @return
     */
    private TypeMapping parserPropertyMapping(TypeMapping typeMapping, ColumnInfo columnInfo) {
        if(columnInfo.getTypeConvertor() != null){
            TypeMapping propertyTypeMapping = new TypeMapping(columnInfo.getColumnMappedField().getType());
            propertyTypeMapping.setTypeConvertor(columnInfo.getTypeConvertor());
            propertyTypeMapping.setColumnName(columnInfo.getColumnName());
            propertyTypeMapping.setMetaField(columnInfo.getColumnMappedField());
            propertyTypeMapping.setParentTypeMapping(typeMapping);
            return propertyTypeMapping;
        }
        return null;
    }

    /**
     * 获取适配的返回类型
     * @param returnType
     * @return
     */
    private Class<?> getAdaptableType(Class<?> returnType){
        if(returnType.isInterface()){
            if(List.class.isAssignableFrom(returnType))
                return ArrayList.class;
            else if(Set.class.isAssignableFrom(returnType))
                return HashSet.class;
            else
                return ArrayList.class;
        }else {
            if(List.class.isAssignableFrom(returnType))
                return ArrayList.class;
            else if(Set.class.isAssignableFrom(returnType))
                return HashSet.class;
            else {
                return returnType;
            }
        }
    }
}
