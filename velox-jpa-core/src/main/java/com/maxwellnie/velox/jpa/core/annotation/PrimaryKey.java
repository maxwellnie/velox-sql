package com.maxwellnie.velox.jpa.core.annotation;

import com.maxwellnie.velox.jpa.core.manager.KeyStrategyManager;
import com.maxwellnie.velox.jpa.core.enums.PrimaryMode;

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
     * 主键的模式：无、jdbc自增，其他
     *
     * @return
     */
    PrimaryMode value() default PrimaryMode.NONE;

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
}
