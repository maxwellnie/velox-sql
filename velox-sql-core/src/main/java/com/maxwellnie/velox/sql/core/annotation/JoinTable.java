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
@Target({ElementType.TYPE})
public @interface JoinTable {
    String value() default "";

    String slaveTableName();

    String masterTableField();

    String slaveTableJoinColumn();

    String slaveTableAlias() default "";

    JoinType joinType() default JoinType.LEFT;

    ;
}
