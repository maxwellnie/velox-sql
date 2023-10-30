package com.maxwellnie.velox.jpa.core.annotation;

import com.maxwellnie.velox.jpa.core.java.type.TypeConvertor;
import com.maxwellnie.velox.jpa.core.java.type.impl.DefaultConvertor;
import com.maxwellnie.velox.jpa.core.manager.KeyStrategyManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 主键注解
 *
 * @author Maxwell Nie
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface PrimaryKey {

    /**
     * 主键策略：默认策略、jdbc自增策略、自定义策略
     *
     * @return
     */
    String strategyKey() default KeyStrategyManager.DEFAULT;

    /**
     * @return 主键名
     */
    String name() default "";

    /**
     * @return 类型转换器
     */
    Class<? extends TypeConvertor> convertor() default DefaultConvertor.class;
}
