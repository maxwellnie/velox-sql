package com.maxwellnie.velox.sql.core.annotation;

import com.maxwellnie.velox.sql.core.natives.enums.JoinType;

import java.lang.annotation.*;

/**
 * @author Maxwell Nie
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface JoinTable {
    String value() default "";
    String slaveTableName();
    String masterTableField();
    String slaveTableColumn();
    String slaveTableAlias() default "";
    boolean isManyToMany() default false;
    JoinType joinType() default JoinType.LEFT;;
}
