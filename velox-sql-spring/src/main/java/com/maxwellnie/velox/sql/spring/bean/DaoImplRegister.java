package com.maxwellnie.velox.sql.spring.bean;

import com.maxwellnie.velox.sql.core.annotation.Entity;
import com.maxwellnie.velox.sql.core.config.simple.SingletonConfiguration;
import com.maxwellnie.velox.sql.core.utils.java.StringUtils;
import com.maxwellnie.velox.sql.spring.bean.factory.DaoImplFactoryBean;
import com.maxwellnie.velox.sql.spring.listener.SpringTransactionSupportInjection;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.util.Assert.notNull;

/**
 * @author Maxwell Nie
 */
public class DaoImplRegister implements BeanDefinitionRegistryPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DaoImplRegister.class);
    private String jdbcSessionFactoryBeanName = "jdbcSessionFactoryBean";
    private String packagePaths;
    private Class<?> daoImplClass;
    private String daoImplClassName;

    public DaoImplRegister() {
    }

    public Class<?> getDaoImplClass() {
        return daoImplClass;
    }

    public void setDaoImplClass(Class<?> daoImplClass) {
        this.daoImplClass = daoImplClass;
    }

    public String getJdbcSessionFactoryBeanName() {
        return jdbcSessionFactoryBeanName;
    }

    public void setJdbcSessionFactoryBeanName(String jdbcSessionFactoryBeanName) {
        this.jdbcSessionFactoryBeanName = jdbcSessionFactoryBeanName;
    }

    private Set<Class<?>> getAllMarkedClassOfPath(String packagePaths) {
        final Set<Class<?>> classSet = new HashSet<>();
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
        for (String packagePath : packagePaths.split(",")) {
            if (StringUtils.isNullOrEmpty(packagePath))
                continue;
            for (BeanDefinition bd : provider.findCandidateComponents(packagePath)) {
                try {
                    Class<?> clazz = Class.forName(bd.getBeanClassName());
                    logger.debug("Get a entity " + clazz.getName() + " ,it's added to clazzArr.");
                    classSet.add(clazz);
                } catch (ClassNotFoundException e) {
                    logger.warn("The class named " + bd.getBeanClassName() + " cannot be properly loaded.");
                }
            }
        }
        return classSet;
    }

    private void register(BeanDefinitionRegistry registry) {
        if(daoImplClass == null){
            if(StringUtils.isNullOrEmpty(daoImplClassName))
                throw new BeanCreationException("The daoImplClass is null.");
            else {
                try {
                    daoImplClass = Class.forName(daoImplClassName);
                } catch (ClassNotFoundException e) {
                    throw new BeanCreationException(e.getMessage());
                }
            }
        }
        SingletonConfiguration.getInstance().setDaoImplClass(daoImplClass);
        registerSpringTransactionSupport(registry);
        for (Class<?> entityClass : getAllMarkedClassOfPath(packagePaths)) {
            try {
                registerDaoBean(entityClass, getDaoImplClass(), registry);
            } catch (Throwable throwable) {
                logger.warn("The daoImplBean of entity " + entityClass.getName() + " cannot be register.");
                throw throwable;
            }
        }
    }
    private void registerSpringTransactionSupport(BeanDefinitionRegistry registry){
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(SpringTransactionSupportInjection.class);
        beanDefinitionBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        registry.registerBeanDefinition("springTransactionSupportInject", beanDefinitionBuilder.getBeanDefinition());
    }
    public String getPackagePaths() {
        return packagePaths;
    }

    public void setPackagePaths(String packagePaths) {
        this.packagePaths = packagePaths;
    }

    public String getDaoImplClassName() {
        return daoImplClassName;
    }

    public void setDaoImplClassName(String daoImplClassName) {
        this.daoImplClassName = daoImplClassName;
    }

    private void registerDaoBean(Class<?> entityClass, Class<?> daoImplClass, BeanDefinitionRegistry registry) {
        notNull(entityClass, "entityClass must be not null.");
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DaoImplFactoryBean.class);
        beanDefinitionBuilder.addPropertyValue("entityClass", entityClass);
        ParameterizedType parameterizedType = new ParameterizedType() {
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
        String beanClassName = entityClass.getSimpleName() +
                this.getDaoImplClass().getName().substring(this.getDaoImplClass().getName().lastIndexOf(".") + 1);
        DynamicType.Builder<?> dynamicTypeBuilder = new ByteBuddy()
                .subclass(Object.class)
                .name(daoImplClass.getName().substring(0, daoImplClass.getName().lastIndexOf(".") + 1) + beanClassName)
                .implement(parameterizedType);
        Class<?> dynamicClass = dynamicTypeBuilder
                .make()
                .load(Thread.currentThread().getContextClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();
        beanDefinitionBuilder.addPropertyValue("daoImplClass", dynamicClass);
        beanDefinitionBuilder.addPropertyValue("jdbcSessionFactory", new RuntimeBeanReference(jdbcSessionFactoryBeanName));
        beanDefinitionBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        String beanName = StringUtils.toFirstLowerCase(entityClass.getSimpleName()) +
                this.getDaoImplClass().getName().substring(this.getDaoImplClass().getName().lastIndexOf(".") + 1);
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