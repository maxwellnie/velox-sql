package com.crazy.sql.core.annotation;

import com.crazy.sql.core.enums.PrimaryMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Akiba no ichiichiyoha
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Table {
    String value() default "";
    int fetchSize() default 0;
}
