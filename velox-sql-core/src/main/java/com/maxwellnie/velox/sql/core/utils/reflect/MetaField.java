package com.maxwellnie.velox.sql.core.utils.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 相对安全的对对象中的字段进行赋值和查询，避免破坏封装性。
 * @author Maxwell Nie
 */
public class MetaField {
    Field field;
    Method getter;
    Method setter;
    boolean isCollection;
    /**
     * 设置对象的属性值
     * @param bean
     * @param value
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    public void set(Object bean, Object value) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if(setter == null){
            throw new NoSuchMethodException("setter not found for " + field.getName());
        }
        setter.invoke(bean, value);
    }
    /**
     * 获取对象的属性值
     * @param bean
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    public Object get(Object bean) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if(getter == null){
            throw new NoSuchMethodException("getter not found for " + field.getName());
        }
        return getter.invoke(bean);
    }
    /**
     * 是否是集合
     * @return
     */
    public boolean isCollection(){
        return isCollection;
    }
    /**
     * 获取字段对象
     * @return
     */
    public Field getField() {
        return field;
    }
    /**
     * 获取字段名
     * @return
     */
    public String getName(){
        return field.getName();
    }
    /**
     * 获取字段类型
     * @return
     */
    public Class<?> getType(){
        return field.getType();
    }
}
