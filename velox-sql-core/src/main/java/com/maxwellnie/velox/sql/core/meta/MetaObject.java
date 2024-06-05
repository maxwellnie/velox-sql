package com.maxwellnie.velox.sql.core.meta;

import com.maxwellnie.velox.sql.core.utils.reflect.MetaField;
import com.maxwellnie.velox.sql.core.utils.reflect.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * 元对象
 * @author Maxwell Nie
 */
public class MetaObject {
    private static final Logger logger = LoggerFactory.getLogger(MetaObject.class);
    /**
     * 被转换的对象
     */
    private final Object obj;
    /**
     * 字段映射
     */
    private final Map<String, MetaField> fieldMap;

    public MetaObject(Object obj, Map<String, MetaField> fieldMap) {
        this.obj = obj;
        this.fieldMap = fieldMap;
    }

    /**
     * 设置元对象属性值
     *
     * @param fieldName
     * @param value
     */
    public void setFieldValue(String fieldName, Object value) {
        MetaField field = fieldMap.get(fieldName);
        if(field == null)
            throw new IllegalArgumentException("not found：" + fieldName);
        try {
            field.set(obj, value);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }
    /**
     * 获取java对象的元对象
     *
     * @param obj
     * @return
     */
    public static MetaObject of(Object obj) {
        Class<?> clazz = obj.getClass();
        Map<String, MetaField> fieldMap = ReflectionUtils.getMetaFieldsMap(clazz);
        return new MetaObject(obj, fieldMap);
    }
    /**
     * 获取元对象属性值
     *
     * @param fieldName
     * @return
     */
    public Object getFieldValue(String fieldName) {
        if(!fieldMap.containsKey(fieldName))
            throw new IllegalArgumentException("not found：" + fieldName);
        try {
            return fieldMap.get(fieldName).get(obj);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }
    public Object getObj() {
        return obj;
    }

    @Override
    public String toString() {
        return obj.toString();
    }
}
