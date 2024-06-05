package com.maxwellnie.velox.sql.spring.boot;

import com.maxwellnie.velox.sql.core.natives.dao.BaseDao;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(DaoImplRegistrar.class)
public @interface DaoImplConf {
    String value() default "";

    String jdbcSessionFactoryBeanName() default "jdbcSessionFactoryBean";

    Class<?> daoImplClass() default BaseDao.class;
}