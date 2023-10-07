package com.maxwellnie.vleox.jpa.core.utils.reflect.meta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 元对象工具
 *
 * @author Maxwell Nie
 */
public class MetaObjectUtils {
    /**
     * 获取java对象的元对象
     *
     * @param obj
     * @return
     */
    public static MetaObject of(Object obj) {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Map<String, Field> fieldMap = Arrays.stream(fields).peek(x -> x.setAccessible(true)).collect(Collectors.toMap(Field::getName, x -> x));
        return new MetaObject(obj, fieldMap);
    }
}
