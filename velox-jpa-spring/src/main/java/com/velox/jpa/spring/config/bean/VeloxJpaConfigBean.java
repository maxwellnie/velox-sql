package com.velox.jpa.spring.config.bean;

import com.velox.jpa.spring.executor.ExecutorUtils;
import com.velox.jpa.spring.transaction.SpringTransactionFactory;
import com.maxwellnie.velox.jpa.core.dao.support.env.Environment;
import com.maxwellnie.velox.jpa.core.config.BaseConfig;
import com.maxwellnie.velox.jpa.core.exception.VeloxImplConfigException;
import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContextFactory;
import com.maxwellnie.velox.jpa.core.jdbc.context.SimpleContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import javax.sql.DataSource;

/**
 * 配置类，可以通过设置来初始化JdbcContextFactory
 *
 * @author Maxwell Nie
 */
public class VeloxJpaConfigBean extends BaseConfig implements InitializingBean,FactoryBean<JdbcContextFactory>, ApplicationListener<ApplicationEvent> {
    private static final Logger logger = LoggerFactory.getLogger(VeloxJpaConfigBean.class);
    private DataSource dataSource;
    private JdbcContextFactory jdbcContextFactory;


    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        //1.0 version ,before it's validate to bean registered method.
    }

    @Override
    public void afterPropertiesSet() {
        if (dataSource == null) {
            logger.error("Your datasource is null.");
            throw new VeloxImplConfigException("The datasource must be not null.");
        }
        Environment environment=new Environment(new SpringTransactionFactory(),dataSource,this);
        this.jdbcContextFactory=new SimpleContextFactory(environment);
        ExecutorUtils.proxyAllExecutor(this.jdbcContextFactory);
    }
    @Override
    public JdbcContextFactory getObject() {
        if(this.jdbcContextFactory == null)
            afterPropertiesSet();
        return this.jdbcContextFactory;
    }

    public JdbcContextFactory getJdbcContextFactory() {
        return jdbcContextFactory;
    }

    public void setJdbcContextFactory(JdbcContextFactory jdbcContextFactory) {
        this.jdbcContextFactory = jdbcContextFactory;
    }

    @Override
    public Class<?> getObjectType() {
        return JdbcContextFactory.class;
    }
}
