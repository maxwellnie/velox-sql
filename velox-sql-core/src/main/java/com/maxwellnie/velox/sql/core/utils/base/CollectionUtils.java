package com.maxwellnie.velox.sql.core.utils.base;

import com.maxwellnie.velox.sql.core.utils.reflect.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Collection工具类
 *
 * @author Maxwell Nie
 */
public class CollectionUtils {
    public static <T> T first(Collection<T> collection) {
        if (collection == null)
            return null;
        Iterator<T> iterator = collection.iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        return iterator.next();
    }

    public static boolean isCollection(Object object) {
        return object != null && Collection.class.isAssignableFrom(object.getClass());
    }

    public static List newList(Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return (List) ReflectionUtils.newInstance(clazz);
    }

    public static Map newMap(Class<?> clazz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return (Map) ReflectionUtils.newInstance(clazz);
    }

    public static Set newSet(Class<?> clazz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return (Set) ReflectionUtils.newInstance(clazz);
    }
}
