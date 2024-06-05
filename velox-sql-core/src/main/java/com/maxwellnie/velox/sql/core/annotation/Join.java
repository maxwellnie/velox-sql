package com.maxwellnie.velox.sql.core.annotation;

import com.maxwellnie.velox.sql.core.natives.enums.JoinType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Maxwell Nie
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Join {
    Class<?> slaveTable();
    String masterTableField();
    String slaveTableField();
    String slaveTableAlias() default "";

    boolean isManyToMany() default false;
    JoinType joinType() default JoinType.LEFT;
}
