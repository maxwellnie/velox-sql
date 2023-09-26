package com.crazy.sql.core.annotation;

import com.crazy.sql.core.proxy.executor.Executor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Akiba no ichiichiyoha
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RegisterMethod {
    Class<Executor> value();
}
