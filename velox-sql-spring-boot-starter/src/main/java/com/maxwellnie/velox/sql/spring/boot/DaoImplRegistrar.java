package com.maxwellnie.velox.sql.spring.boot;

import com.maxwellnie.velox.sql.core.utils.java.StringUtils;
import com.maxwellnie.velox.sql.spring.bean.DaoImplRegister;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Maxwell Nie
 */
public class DaoImplRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        AnnotationAttributes attributes = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(DaoImplConf.class.getName()));
        if (attributes != null) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DaoImplRegister.class);

            String jdbcSessionFactoryBeanName = attributes.getString("jdbcSessionFactoryBeanName");
            if (!StringUtils.isNullOrEmpty(jdbcSessionFactoryBeanName)) {
                builder.addPropertyValue("jdbcSessionFactoryBeanName", jdbcSessionFactoryBeanName);
            }
            String packagePaths = attributes.getString("value");
            if (!StringUtils.isNullOrEmpty(packagePaths)) {
                builder.addPropertyValue("packagePaths", packagePaths);
            }
            Class<?> daoImplClass = attributes.getClass("daoImplClass");
            builder.addPropertyValue("daoImplClass", daoImplClass);
            registry.registerBeanDefinition("veloxSqlRegister", builder.getBeanDefinition());
        }
    }
}
