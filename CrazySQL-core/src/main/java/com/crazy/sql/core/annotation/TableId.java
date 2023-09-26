package com.crazy.sql.core.annotation;

import com.crazy.sql.core.enums.PrimaryMode;
import com.crazy.sql.core.manager.KeyStrategyManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Akiba no ichiichiyoha
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface TableId {
    PrimaryMode value() default PrimaryMode.NONE;
    String strategyKey() default KeyStrategyManager.DEFAULT;
    String name() default "";
}
