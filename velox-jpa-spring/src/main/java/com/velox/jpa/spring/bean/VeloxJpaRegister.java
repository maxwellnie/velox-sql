package com.velox.jpa.spring.bean;

import com.maxwellnie.velox.jpa.core.annotation.Entity;
import com.maxwellnie.velox.jpa.core.config.BaseConfig;
import com.velox.jpa.spring.bean.factory.DaoImplFactoryBean;
import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.util.Assert.notNull;

/**
 * @author Maxwell Nie
 */
public class VeloxJpaRegister implements BeanDefinitionRegistryPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(VeloxJpaRegister.class);
    private String jdbcContextFactoryBeanName = "veloxJpaConfigBean";
    private String packagePath;
    private String daoImplClassName;

    public VeloxJpaRegister() {
    }
    public String getDaoImplClassName() {
        return daoImplClassName;
    }

    public void setDaoImplClassName(String daoImplClassName) {
        this.daoImplClassName = daoImplClassName;
    }

    public String getJdbcContextFactoryBeanName() {
        return jdbcContextFactoryBeanName;
    }

    public void setJdbcContextFactoryBeanName(String jdbcContextFactoryBeanName) {
        this.jdbcContextFactoryBeanName = jdbcContextFactoryBeanName;
    }
    private Set<Class<?>> getAllMarkedClassOfPath(String packagePath) {
        final Set<Class<?>> classSet = new HashSet<>();
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
        for (BeanDefinition bd : provider.findCandidateComponents(packagePath)) {
            try {
                Class<?> clazz = Class.forName(bd.getBeanClassName());
                logger.debug("Get a entity " + clazz.getName() + " ,it's added to clazzArr.");
                classSet.add(clazz);
            } catch (ClassNotFoundException e) {
                logger.warn("The class named " + bd.getBeanClassName() + " cannot be properly loaded.");
            }
        }
        return classSet;
    }
    private void register(BeanDefinitionRegistry registry) {
        BaseConfig.setDaoImplClassName(this.daoImplClassName);
        for (Class<?> entityClass : getAllMarkedClassOfPath(packagePath)) {
            try {
                registerBean(entityClass,Class.forName(BaseConfig.getDaoImplClassName()),registry);
            } catch (Throwable throwable) {
                logger.warn("The daoImplBean of entity " + entityClass.getName() + " cannot be register.");
                try {
                    throw throwable;
                } catch (ClassNotFoundException e) {
                    logger.error("The daoImplClass "+ BaseConfig.getDaoImplClassName()+" is not found.");
                    e.printStackTrace();
                }
            }
        }
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    private void registerBean(Class<?> entityClass, Class<?> daoImplClass, BeanDefinitionRegistry registry) {
        notNull(entityClass, "entityClass must be not null.");
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DaoImplFactoryBean.class);
        beanDefinitionBuilder.addPropertyValue("entityClass", entityClass);
        ParameterizedType parameterizedType=new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{entityClass};
            }

            @Override
            public Type getRawType() {
                return daoImplClass;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
        DynamicType.Builder<?> dynamicTypeBuilder = new ByteBuddy()
                .subclass(Object.class)
                .implement(parameterizedType);
        Class<?> dynamicClass = dynamicTypeBuilder
                .make()
                .load(Thread.currentThread().getContextClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();
        beanDefinitionBuilder.addPropertyValue("daoImplClass",dynamicClass);
        beanDefinitionBuilder.addPropertyValue("jdbcContextFactory",new RuntimeBeanReference(jdbcContextFactoryBeanName));
        beanDefinitionBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        String beanName = StringUtils.toFirstLowerCase(entityClass.getSimpleName()) +
                BaseConfig.getDaoImplClassName().substring(BaseConfig.getDaoImplClassName().lastIndexOf(".") + 1);
        if (registry.isBeanNameInUse(beanName))
            throw new BeanCreationException("The bean named " + beanName + " is used.");
        else
            registry.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        register(beanDefinitionRegistry);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        //undo
    }

}