package com.maxwellnie.velox.jpa.spring.boot;

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
    String jdbcContextFactoryBeanName() default "jdbcContextFactoryBean";
    String daoImplClassName() default "com.maxwellnie.velox.jpa.core.template.dao.TemplateDao";
}