package com.crazy.sql.core.manager;

import com.crazy.sql.core.java.type.impl.*;
import com.crazy.sql.core.java.type.TypeConvertor;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 类型转换器管理器
 * @author Akiba no ichiichiyoha
 */
public class ConvertorManager {
    private static final Map<Class, TypeConvertor> typeConvertorMap= Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * 默认的转换逻辑
     */
    static {
        typeConvertorMap.put(int.class,new IntegerConvertor());
        typeConvertorMap.put(Integer.class,new IntegerConvertor());
        typeConvertorMap.put(byte.class,new ByteConvertor());
        typeConvertorMap.put(Byte.class,new ByteConvertor());
        typeConvertorMap.put(short.class,new ShortConvertor());
        typeConvertorMap.put(Short.class,new ShortConvertor());
        typeConvertorMap.put(long.class,new LongConvertor());
        typeConvertorMap.put(Long.class,new LongConvertor());
        typeConvertorMap.put(float.class,new FloatConvertor());
        typeConvertorMap.put(Float.class,new FloatConvertor());
        typeConvertorMap.put(double.class,new DoubleConvertor());
        typeConvertorMap.put(Double.class,new DoubleConvertor());
        typeConvertorMap.put(boolean.class,new BooleanConvertor());
        typeConvertorMap.put(Boolean.class,new BooleanConvertor());
        typeConvertorMap.put(Date.class,new DateConvertor());
    }

    /**
     * 默认的转换器
     */
    public static final TypeConvertor defaultConvertor=new DefaultConvertor();

    /**
     * 获取类型转换器，注意，这个管理器不能注册类型转换器！
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> TypeConvertor<T> getConvertor(Class<?> clazz){
        TypeConvertor<T> typeConvertor=typeConvertorMap.get(clazz);
        if(typeConvertor==null)
            return defaultConvertor;
        else
            return typeConvertor;
    }
}
