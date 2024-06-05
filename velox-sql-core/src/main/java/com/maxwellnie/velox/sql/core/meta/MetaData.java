package com.maxwellnie.velox.sql.core.meta;

import com.maxwellnie.velox.sql.core.utils.reflect.MetaField;
import com.maxwellnie.velox.sql.core.utils.reflect.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 元数据
 *
 * @author Maxwell Nie
 */
public class MetaData {
    private static final Logger logger = LoggerFactory.getLogger(MetaData.class);
    private final Map<String, Object> meta;

    private MetaData(Map<String, Object> meta) {
        this.meta = meta;
    }
    @SuppressWarnings("uncheck")
    public static MetaData of(Object o) {
        Class<?> clazz = o.getClass();
        Map<String, MetaField> fields = ReflectionUtils.getMetaFieldsMap(clazz);
        Map<String, Object> fieldMap = Collections.synchronizedMap(fields.keySet().stream().collect(Collectors.toMap(k->k, (field) -> {
            try {
                return fields.get(field).get(o);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                logger.error(e.getMessage() + "\t\n" + e.getCause());
                return null;
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
    public void addProperty(Field field, Object bean){
        addProperty(field.getName(), bean);
    }
    public<T> T getProperty(Field field, Class<T> clazz) {
        return (T) this.meta.get(field.getName());
    }
    public<T> T getProperty(String name) {
        return (T) this.meta.get(name);
    }
    public boolean isForClass(Class<?> beanClass){
        if (this.meta.isEmpty())
            return false;
        else {
            List<Field> fields = ReflectionUtils.getAllFields(beanClass);
            return isForClass0(fields);
        }
    }
    public void addFromMetaData(MetaData metaData){
        this.meta.putAll(metaData.meta);
    }
    private boolean isForClass0(List<Field> fields){
        if (fields.isEmpty())
            return false;
        else {
            for (Field field:fields){
                if(!this.meta.containsKey(field.getName()))
                    return false;
            }
            return true;
        }
    }
    private boolean isForClass1(Collection<String> fields){
        if (fields.isEmpty())
            return false;
        else {
            for (String field:fields){
                if(!this.meta.containsKey(field))
                    return false;
            }
            return true;
        }
    }
    public <T> T getBeanInstance(Class<T> beanClazz) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        assert beanClazz != null : "Class instance must not be null.";
        Map<String, MetaField> fieldMap = ReflectionUtils.getMetaFieldsMap(beanClazz);
        assert isForClass1(fieldMap.keySet()) : "The class instance does not match the metadata.";
        T object = ReflectionUtils.newInstance(beanClazz);
        for (MetaField field:ReflectionUtils.getMetaFieldsMap(beanClazz).values()){
            field.set(object, meta.get(field.getName()));
        }
        return object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetaData metaData = (MetaData) o;
        return Objects.equals(meta, metaData.meta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meta);
    }

    @Override
    public String toString() {
        return meta.toString();
    }
}
