package com.velox.jpa.spring.bean;

import com.velox.jpa.spring.bean.factory.DaoImplFactoryBean;
import com.velox.jpa.spring.config.bean.VeloxJpaConfigBean;
import com.maxwellnie.vleox.jpa.core.dao.support.env.Environment;
import com.maxwellnie.vleox.jpa.core.jdbc.context.JdbcContextFactory;
import com.maxwellnie.vleox.jpa.core.utils.java.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

import static org.springframework.util.Assert.notNull;

/**
 * 后处理器在xml配置文件的bean全部注册完成后处理DaoImpl的注册
 *
 * @author Maxwell Nie
 */
@Component
public class DaoImplRegister implements BeanDefinitionRegistryPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DaoImplRegister.class);
    private VeloxJpaConfigBean veloxJpaConfigBean;
    private JdbcContextFactory jdbcContextFactory;

    public VeloxJpaConfigBean getVeloxImplConfigBean() {
        return veloxJpaConfigBean;
    }

    public void setVeloxImplConfigBean(VeloxJpaConfigBean veloxJpaConfigBean) {
        this.veloxJpaConfigBean = veloxJpaConfigBean;
    }

    public JdbcContextFactory getJdbcContextFactory() {
        return jdbcContextFactory;
    }

    public void setJdbcContextFactory(JdbcContextFactory jdbcContextFactory) {
        this.jdbcContextFactory = jdbcContextFactory;
    }

    private void register(BeanDefinitionRegistry registry) {
        for (Class<?> entityClass : veloxJpaConfigBean.getClazzArr()) {
            try {
                registerBean(entityClass, registry);
            } catch (Throwable throwable) {
                logger.warn("The daoImplBean of entity " + entityClass.getName() + " cannot be register.");
                throw throwable;
            }
        }
    }

    private void registerBean(Class<?> entityClass, BeanDefinitionRegistry registry) {
        notNull(entityClass, "entityClass must be not null.");
        notNull(veloxJpaConfigBean.getDaoImplClazz(), "daoImplInterface must be not null.");
        notNull(jdbcContextFactory, "jdbcContextFactory must be not null.");
        Environment environment = jdbcContextFactory.getEnvironment();
        notNull(environment.getDaoImplFactory(entityClass), "daoImplFactory must be not null.");
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DaoImplFactoryBean.class);
        beanDefinitionBuilder.addPropertyValue("daoImplInterface", veloxJpaConfigBean.getDaoImplClazz());
        beanDefinitionBuilder.addPropertyValue("daoImplFactory", environment.getDaoImplFactory(entityClass));
        beanDefinitionBuilder.addPropertyValue("JdbcContext", jdbcContextFactory.produce());
        beanDefinitionBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        String beanName = StringUtils.toFirstLowerCase(entityClass.getSimpleName()) + veloxJpaConfigBean.getDaoImplClazz().getSimpleName();
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
