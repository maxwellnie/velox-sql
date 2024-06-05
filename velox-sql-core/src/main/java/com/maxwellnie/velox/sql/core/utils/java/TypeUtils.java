package com.maxwellnie.velox.sql.core.utils.java;

import java.util.Collection;

/**
 * @author Maxwell Nie
 */
public class TypeUtils {
    public static boolean isCollection(Class<?> clazz){
        return Collection.class.isAssignableFrom(clazz);
    }
    public static boolean isMap(Class<?> clazz){
        return java.util.Map.class.isAssignableFrom(clazz);
    }
    public static boolean isArray(Class<?> clazz){
        return clazz.isArray();
    }
    public static boolean isSet(Class<?> clazz){
        return java.util.Set.class.isAssignableFrom(clazz);
    }
    public static boolean isList(Class<?> clazz){
        return java.util.List.class.isAssignableFrom(clazz);
    }
}
