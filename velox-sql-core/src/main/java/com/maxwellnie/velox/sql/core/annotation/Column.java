package com.maxwellnie.velox.sql.core.annotation;

import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;
import com.maxwellnie.velox.sql.core.natives.type.convertor.impl.DefaultConvertor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 列注解
 *
 * @author Maxwell Nie
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Column {
    /**
     * @return 列名
     */
    String value() default "";

    /**
     * @return 是否被排除
     */
    boolean isExclusion() default false;

    /**
     * @return 类型转换器
     */
    Class<? extends TypeConvertor> convertor() default DefaultConvertor.class;
}
