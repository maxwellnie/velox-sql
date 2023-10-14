package com.maxwellnie.velox.jpa.spring.bean.factory;

import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContextFactory;
import com.maxwellnie.velox.jpa.core.proxy.DaoImplFactory;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Type;

import static org.springframework.util.Assert.notNull;

/**
 * @author Maxwell Nie
 */
public class DaoImplFactoryBean<T> implements FactoryBean<T>{
    private Type type;
    private DaoImplFactory<T> daoImplFactory;
    private Class<?> entityClass;
    private Class<?> daoImplClass;
    private JdbcContextFactory jdbcContextFactory;

    @Override
    public T getObject() {
        notNull(entityClass,"DaoImplFactoryBean need an class object of entity.");
        if(daoImplFactory==null){
            jdbcContextFactory.getEnvironment().addDaoImpl(entityClass);
            daoImplFactory= jdbcContextFactory.getEnvironment().getDaoImplFactory(entityClass);
        }
        return daoImplFactory.produce(null);
    }

    @Override
    public Class<?> getObjectType() {
        return this.daoImplClass;
    }

    public DaoImplFactory<T> getDaoImplFactory() {
        return daoImplFactory;
    }

    public void setDaoImplFactory(DaoImplFactory<T> daoImplFactory) {
        this.daoImplFactory = daoImplFactory;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public Class<?> getDaoImplClass() {
        return daoImplClass;
    }

    public void setDaoImplClass(Class<?> daoImplClass) {
        this.daoImplClass = daoImplClass;
    }

    public JdbcContextFactory getJdbcContextFactory() {
        return jdbcContextFactory;
    }

    public void setJdbcContextFactory(JdbcContextFactory jdbcContextFactory) {
        this.jdbcContextFactory = jdbcContextFactory;
    }
}
