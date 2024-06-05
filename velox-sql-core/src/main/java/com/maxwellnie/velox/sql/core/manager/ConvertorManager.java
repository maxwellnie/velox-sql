package com.maxwellnie.velox.sql.core.manager;

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
    private static final Map<Class<?>, TypeConvertor<?>> typeConvertorMap = new LinkedHashMap<>();

    /**
     * 默认的转换逻辑
     */
    static {
        typeConvertorMap.put(int.class, new IntegerConvertor());
        typeConvertorMap.put(Integer.class, new IntegerConvertor());
        typeConvertorMap.put(byte.class, new ByteConvertor());
        typeConvertorMap.put(Byte.class, new ByteConvertor());
        typeConvertorMap.put(short.class, new ShortConvertor());
        typeConvertorMap.put(Short.class, new ShortConvertor());
        typeConvertorMap.put(long.class, new LongConvertor());
        typeConvertorMap.put(Long.class, new LongConvertor());
        typeConvertorMap.put(float.class, new FloatConvertor());
        typeConvertorMap.put(Float.class, new FloatConvertor());
        typeConvertorMap.put(double.class, new DoubleConvertor());
        typeConvertorMap.put(Double.class, new DoubleConvertor());
        typeConvertorMap.put(boolean.class, new BooleanConvertor());
        typeConvertorMap.put(Boolean.class, new BooleanConvertor());
        typeConvertorMap.put(Date.class, new DateConvertor());
        typeConvertorMap.put(Object.class, new NoTypeConvertor());
        typeConvertorMap.put(BigDecimal.class, new BigDecimalConvertor());
        typeConvertorMap.put(Timestamp.class, new TimestampConvertor());
    }

    /**
     * 获取类型转换器，注意，这个管理器不能注册类型转换器！
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> TypeConvertor<T> getConvertor(Class<?> clazz) {
        TypeConvertor<T> typeConvertor = (TypeConvertor<T>) typeConvertorMap.get(clazz);
        if (typeConvertor == null)
            return (TypeConvertor<T>) DEFAULT_CONVERTOR;
        else
            return typeConvertor;
    }
}
