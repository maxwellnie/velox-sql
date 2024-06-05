package com.maxwellnie.velox.sql.core.annotation;

import com.maxwellnie.velox.sql.core.natives.enums.JoinType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 实体注解
 *
 * @author Maxwell Nie
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Entity {
    /**
     * @return 实体对应数据库表名
     */
    String value() default "";

    /**
     * @return 一次性从数据库查询数据条目
     */
    int fetchSize() default 0;
}
