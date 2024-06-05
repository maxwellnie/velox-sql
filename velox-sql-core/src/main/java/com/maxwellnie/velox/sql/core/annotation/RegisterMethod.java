package com.maxwellnie.velox.sql.core.annotation;

import com.maxwellnie.velox.sql.core.proxy.executor.MethodExecutor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识接口中操作数据库方法的执行器
 *
 * @author Maxwell Nie
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RegisterMethod {
    /**
     * @return 被指定的执行器。
     */
    Class<? extends MethodExecutor> value();

}
