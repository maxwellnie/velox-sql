package com.maxwellnie.velox.sql.spring.bean.factory;

import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSessionFactory;
import com.maxwellnie.velox.sql.core.proxy.DaoImplFactory;
import org.springframework.beans.factory.FactoryBean;

import static org.springframework.util.Assert.notNull;

/**
 * @author Maxwell Nie
 */
public class DaoImplFactoryBean<T> implements FactoryBean<T> {
    private DaoImplFactory<T> daoImplFactory;
    private Class<?> entityClass;
    private Class<?> daoImplClass;
    private JdbcSessionFactory jdbcSessionFactory;

    @Override
    public T getObject() {
        notNull(entityClass, "DaoImplFactoryBean need an class object of entity.");
        if (daoImplFactory == null) {
            jdbcSessionFactory.getHolderObject().addDaoImpl(entityClass);
            daoImplFactory = jdbcSessionFactory.getHolderObject().getDaoImplFactory(entityClass);
        }
        return daoImplFactory.produce(null, jdbcSessionFactory.getHolderObject().getMethodMappedManager());
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

    public JdbcSessionFactory getJdbcSessionFactory() {
        return jdbcSessionFactory;
    }

    public void setJdbcSessionFactory(JdbcSessionFactory jdbcSessionFactory) {
        this.jdbcSessionFactory = jdbcSessionFactory;
    }
}
