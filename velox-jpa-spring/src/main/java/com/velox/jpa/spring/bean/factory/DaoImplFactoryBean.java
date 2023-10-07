package com.velox.jpa.spring.bean.factory;

import com.maxwellnie.vleox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.vleox.jpa.core.proxy.DaoImplFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author Maxwell Nie
 */
public class DaoImplFactoryBean<T> implements FactoryBean<T> {
    private Class<T> daoImplInterface;
    private DaoImplFactory<T> daoImplFactory;
    private JdbcContext jdbcContext;

    @Override
    public T getObject() throws Exception {
        return daoImplFactory.produce(jdbcContext);
    }

    @Override
    public Class<?> getObjectType() {
        return this.daoImplInterface;
    }
}
