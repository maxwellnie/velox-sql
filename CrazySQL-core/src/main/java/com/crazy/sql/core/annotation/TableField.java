package com.crazy.sql.core.annotation;

import com.crazy.sql.core.java.type.impl.DefaultConvertor;
import com.crazy.sql.core.java.type.TypeConvertor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author Akiba no ichiichiyoha
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface TableField {
    String value() default "";
    boolean isExclusion() default false;
    Class<? extends TypeConvertor> convertor() default DefaultConvertor.class;
}
