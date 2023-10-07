package com.maxwellnie.vleox.jpa.core.utils.reflect.meta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Maxwell Nie
 */
public class MetaData {
    private final Map<String, Object> meta;

    private MetaData(Map<String, Object> meta) {
        this.meta = meta;
    }

    public static MetaData of(Object o) {
        Class<?> clazz = o.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Map<String, Object> fieldMap = Collections.synchronizedMap(Arrays.stream(fields).peek(x -> x.setAccessible(true)).collect(Collectors.toMap(Field::getName, (field) -> {
            try {
                return field.get(o);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        })));
        return new MetaData(fieldMap);
    }

    public static MetaData ofEmpty() {
        return new MetaData(Collections.synchronizedMap(new LinkedHashMap<>()));
    }

    public void addProperty(String name, Object bean) {
        this.meta.put(name, bean);
    }

    public Object getProperty(String name) {
        return this.meta.get(name);
    }

    public <T> T getBeanInstance(Class<T> clazz) throws InstantiationException, IllegalAccessException, NoSuchFieldException {
        assert clazz != null : "Class instance must not be null.";
        T object = clazz.newInstance();
        for (Map.Entry<String, Object> entry : meta.entrySet()) {
            Field field = clazz.getField(entry.getKey());
            field.setAccessible(true);
            field.set(object, entry.getValue());
        }
        return object;
    }

}
