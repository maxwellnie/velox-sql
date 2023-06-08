package com.crazy.sql.core.utils;

import com.crazy.sql.core.exception.TableIdNotBoundException;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射工具
 * @param <T>
 */
public class ReflectUtils <T>{
    private Class<T> tClass;
    private Method[] methods;
    private Field[] fields;
    private String tableSuffix="";
    private boolean standColumnName=false;
    private String primaryKey;
    private Field primaryKeyField;

    public ReflectUtils(Class<T> tClass,String tableSuffix,boolean standColumnName) {
        this.tClass = tClass;
        this.methods=tClass.getDeclaredMethods();
        this.fields=tClass.getDeclaredFields();
        this.tableSuffix=tableSuffix;
        this.standColumnName=standColumnName;
        for (Field f:fields) {
            if(f.isAnnotationPresent(Id.class)) {
                this.primaryKeyField=f;
                this.primaryKey = f.getName();
                return;
            }
        }
        new TableIdNotBoundException().printStackTrace();
    }

    /**
     * 获取主键的名字
     * @return
     */
    public String getPrimaryKey(){
        return handleColumnName(this.primaryKey);
    }
    /**
     * 获取主键的名字
     * @return
     */
    public String getNotStandPrimaryKey(){
        return this.primaryKey;
    }
    public Field getPrimaryKeyField(){
        return this.primaryKeyField;
    }
    /**
     * 获取主键值
     * @param t
     * @return
     */
    public Object getPrimaryKeyValue(T t){
        for (Field f:fields) {
            if(f.isAnnotationPresent(Id.class)) {
                try {
                    f.setAccessible(true);
                    return f.get(t);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        new TableIdNotBoundException().printStackTrace();
        return null;
    }

    /**
     * 获取Setter的名字
     * @param field
     * @return
     */
    public String getSetterName(String field){
        return "set"+(field.charAt(0)+"").toUpperCase()+field.substring(1,field.length());
    }

    /**
     * 获取Setter方法的对象
     * @param field
     * @return
     */
    public Method getSetter(Field field){
        try {
            return tClass.getDeclaredMethod(getSetterName(field.getName()),field.getType());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取实体类对应数据库表的表名
     * @return
     */
    public String getTableName(){
        return tableSuffix+tClass.getSimpleName().toLowerCase();
    }
    public void setTableSuffix(String tableSuffix) {
        this.tableSuffix = tableSuffix;
    }

    /**
     * 对实体类字段名进行处理，处理为数据库中所需要的字段形式例如：userId->user_id
     * @param column
     * @return
     */
    public String handleColumnName(String column){
        if(standColumnName)
            return StringUtils.getStandName(column);
        else
            return column;
    }

    /**
     * 判断这个字段是否为主键
     * @param field
     * @return
     */
    public boolean isPrimaryKey(Field field){
        return field.equals(primaryKeyField);
    }
    public Method[] getMethods() {
        return methods;
    }

    public Field[] getFields() {
        return fields;
    }

    public String getTableSuffix() {
        return tableSuffix;
    }
    public boolean isStandColumnName() {
        return standColumnName;
    }

    public Class<T> gettClass() {
        return tClass;
    }
}
