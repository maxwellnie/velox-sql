package com.velox.jpa.spring.config.bean;

import com.maxwellnie.vleox.jpa.core.utils.CollectionUtils;
import com.velox.jpa.spring.transaction.SpringTransactionFactory;
import com.maxwellnie.vleox.jpa.core.dao.support.env.Environment;
import com.maxwellnie.vleox.jpa.core.annotation.Entity;
import com.maxwellnie.vleox.jpa.core.config.BaseConfig;
import com.maxwellnie.vleox.jpa.core.exception.VeloxImplConfigException;
import com.maxwellnie.vleox.jpa.core.jdbc.context.JdbcContextFactory;
import com.maxwellnie.vleox.jpa.core.jdbc.context.SimpleContextFactory;
import com.maxwellnie.vleox.jpa.core.utils.java.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Set;

/**
 * 配置类，可以通过设置来初始化JdbcContextFactory
 *
 * @author Maxwell Nie
 */
public class VeloxJpaConfigBean extends BaseConfig implements InitializingBean, FactoryBean<JdbcContextFactory>, ApplicationListener<ApplicationEvent> {
    private static final Logger logger = LoggerFactory.getLogger(VeloxJpaConfigBean.class);
    private DataSource dataSource;
    private String packagePath;
    private JdbcContextFactory jdbcContextFactory;

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {

    }

    @Override
    public void afterPropertiesSet() {
        if (dataSource == null) {
            logger.error("Your datasource is null.");
            throw new VeloxImplConfigException("The datasource must be not null.");
        }
        if (StringUtils.isNullOrEmpty(packagePath)) {
            logger.error("Your package path - " + packagePath + " - is empty.");
            throw new VeloxImplConfigException("The packagePath must be not empty.");
        } else {
            Set<Class<?>> classSet = getAllMarkedClassOfPath(packagePath);
            this.setClazzArr(CollectionUtils.toClassArray(classSet));
            Environment environment = new Environment(new SpringTransactionFactory(), dataSource, this);
            jdbcContextFactory = new SimpleContextFactory(environment);
        }

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

    @Override
    public JdbcContextFactory getObject() throws Exception {
        if (this.jdbcContextFactory != null) {
            afterPropertiesSet();
        }
        return this.jdbcContextFactory;
    }

    @Override
    public Class<?> getObjectType() {
        return JdbcContextFactory.class;
    }
}
