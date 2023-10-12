package com.maxwellnie.velox.jpa.spring.boot;

import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;
import com.velox.jpa.spring.bean.DaoImplRegister;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Maxwell Nie
 */
public class DaoImplRegistrar implements ImportBeanDefinitionRegistrar{

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        AnnotationAttributes attributes = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(DaoImplConf.class.getName()));
        if (attributes != null) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DaoImplRegister.class);

            String  jdbcContextFactoryBeanName= attributes.getString("jdbcContextFactoryBeanName");
            if (!StringUtils.isNullOrEmpty(jdbcContextFactoryBeanName)) {
                builder.addPropertyValue("jdbcContextFactoryBeanName", jdbcContextFactoryBeanName);
            }
            String  packagePath= attributes.getString("value");
            if (!StringUtils.isNullOrEmpty(packagePath)) {
                builder.addPropertyValue("packagePath", packagePath);
            }
            String  daoImplClassName= attributes.getString("daoImplClassName");
            if (!StringUtils.isNullOrEmpty(daoImplClassName)) {
                builder.addPropertyValue("daoImplClassName", daoImplClassName);
            }
            registry.registerBeanDefinition("veloxJpaRegister", builder.getBeanDefinition());
        }
    }
}
