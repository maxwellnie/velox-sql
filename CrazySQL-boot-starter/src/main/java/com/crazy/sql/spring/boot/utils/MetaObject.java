package com.crazy.sql.spring.boot.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class MetaObject {
    private Object obj;
    private Map<String, Field> fieldMap;

    public MetaObject(Object obj, Map<String, Field> fieldMap) {
        this.obj = obj;
        this.fieldMap = fieldMap;
    }
    public void set(String fieldName,Object value){
        Field field=fieldMap.get(fieldName);
        field.setAccessible(true);
        try {
            field.set(obj,value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public Object get(String fieldName){
        try {
            return fieldMap.get(fieldName).get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
