package com.crazy.sql.spring.boot.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class MetaObjectUtils {
    public static MetaObject getMetaObject(Object obj){
        Class clazz=obj.getClass();
        Method[] methods=clazz.getDeclaredMethods();
        Field[] fields=clazz.getDeclaredFields();
        Map<String,Field> fieldMap=Arrays.stream(fields).peek(x-> x.setAccessible(true)).collect(Collectors.toMap(Field::getName, x->x));
        return new MetaObject(obj,fieldMap);
    }
}
