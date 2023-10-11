package com.maxwellnie.velox.jpa.core.utils.reflect.meta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 元对象
 *
 * @author Maxwell Nie
 */
public class MetaObject {
    private static final Logger logger= LoggerFactory.getLogger(MetaObject.class);
    /**
     * 被转换的对象
     */
    private final Object obj;
    /**
     * 字段映射
     */
    private final Map<String, Field> fieldMap;

    public MetaObject(Object obj, Map<String, Field> fieldMap) {
        this.obj = obj;
        this.fieldMap = fieldMap;
    }

    /**
     * 设置元对象属性值
     *
     * @param fieldName
     * @param value
     */
    public void set(String fieldName, Object value) {
        Field field = fieldMap.get(fieldName);
        field.setAccessible(true);
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage()+"\t\n"+e.getCause());
        }
    }

    /**
     * 获取元对象属性值
     *
     * @param fieldName
     * @return
     */
    public Object get(String fieldName) {
        try {
            return fieldMap.get(fieldName).get(obj);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage()+"\t\n"+e.getCause());
            return null;
        }
    }
}
