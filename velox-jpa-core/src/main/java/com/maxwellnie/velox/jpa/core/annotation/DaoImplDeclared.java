package com.maxwellnie.velox.jpa.core.annotation;

import com.maxwellnie.velox.jpa.core.proxy.executor.DefaultMethodMapRegister;
import com.maxwellnie.velox.jpa.core.proxy.executor.MethodMapRegister;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Maxwell Nie
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DaoImplDeclared {
    Class<? extends MethodMapRegister> value() default DefaultMethodMapRegister.class;
}
