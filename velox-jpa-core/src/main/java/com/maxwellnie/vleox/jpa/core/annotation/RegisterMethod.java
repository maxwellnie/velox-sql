package com.maxwellnie.vleox.jpa.core.annotation;

import com.maxwellnie.vleox.jpa.core.proxy.executor.Executor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识访问器方法的执行器
 *
 * @author Maxwell Nie
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RegisterMethod {
    /**
     * @return 被指定的执行器
     */
    Class<? extends Executor> value();
}
