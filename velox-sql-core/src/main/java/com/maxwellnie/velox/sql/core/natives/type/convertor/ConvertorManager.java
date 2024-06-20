package com.maxwellnie.velox.sql.core.natives.type.convertor;

import com.maxwellnie.velox.sql.core.natives.registry.Registry;
import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;
import com.maxwellnie.velox.sql.core.natives.type.convertor.impl.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 类型转换器管理器
 *
 * @author Maxwell Nie
 */
public class ConvertorManager {
    /**
     * 默认的转换器
     */
    public static final TypeConvertor<?> DEFAULT_CONVERTOR = new DefaultConvertor();
    public static final String REGISTRY_NAME = "velox-sql:convertor:";
    private static final Registry REGISTRY = Registry.INSTANCE;

    /**
     * 默认的转换逻辑
     */
    static {
        REGISTRY.register(REGISTRY_NAME + int.class.getName(), new IntegerConvertor());
        REGISTRY.register(REGISTRY_NAME + Integer.class.getName(), new IntegerConvertor());
        REGISTRY.register(REGISTRY_NAME + byte.class.getName(), new ByteConvertor());
        REGISTRY.register(REGISTRY_NAME + Byte.class.getName(), new ByteConvertor());
        REGISTRY.register(REGISTRY_NAME + short.class.getName(), new ShortConvertor());
        REGISTRY.register(REGISTRY_NAME + Short.class.getName(), new ShortConvertor());
        REGISTRY.register(REGISTRY_NAME + long.class.getName(), new LongConvertor());
        REGISTRY.register(REGISTRY_NAME + Long.class.getName(), new LongConvertor());
        REGISTRY.register(REGISTRY_NAME + float.class.getName(), new FloatConvertor());
        REGISTRY.register(REGISTRY_NAME + Float.class.getName(), new FloatConvertor());
        REGISTRY.register(REGISTRY_NAME + double.class.getName(), new DoubleConvertor());
        REGISTRY.register(REGISTRY_NAME + Double.class.getName(), new DoubleConvertor());
        REGISTRY.register(REGISTRY_NAME + boolean.class.getName(), new BooleanConvertor());
        REGISTRY.register(REGISTRY_NAME + Boolean.class.getName(), new BooleanConvertor());
        REGISTRY.register(REGISTRY_NAME + Date.class.getName(), new DateConvertor());
        REGISTRY.register(REGISTRY_NAME + Object.class.getName(), new NoTypeConvertor());
        REGISTRY.register(REGISTRY_NAME + BigDecimal.class.getName(), new BigDecimalConvertor());
        REGISTRY.register(REGISTRY_NAME + Timestamp.class.getName(), new TimestampConvertor());
    }

    /**
     * 获取类型转换器，注意，这个管理器不能注册类型转换器！
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> TypeConvertor<T> getConvertor(Class<?> clazz) {
        TypeConvertor<T> typeConvertor = REGISTRY.getValue(REGISTRY_NAME + clazz.getName());
        if (typeConvertor == null)
            return (TypeConvertor<T>) DEFAULT_CONVERTOR;
        else
            return typeConvertor;
    }
}
