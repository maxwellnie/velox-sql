package com.maxwellnie.velox.jpa.spring.config.bean;

import com.maxwellnie.velox.jpa.core.config.BaseConfig;
import com.maxwellnie.velox.jpa.core.dao.support.env.Environment;
import com.maxwellnie.velox.jpa.core.exception.VeloxImplConfigException;
import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContextFactory;
import com.maxwellnie.velox.jpa.core.jdbc.context.SimpleContextFactory;
import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;
import com.maxwellnie.velox.jpa.core.utils.reflect.TableInfoUtils;
import com.maxwellnie.velox.jpa.spring.executor.ExecutorUtils;
import com.maxwellnie.velox.jpa.spring.transaction.SpringTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;

/**
 * 配置类，可以通过设置来初始化JdbcContextFactory
 *
 * @author Maxwell Nie
 */
public class JdbcContextFactoryBean extends BaseConfig implements InitializingBean, FactoryBean<JdbcContextFactory>, ApplicationListener<ApplicationEvent> {
    private static final Logger logger = LoggerFactory.getLogger(JdbcContextFactoryBean.class);
    private DataSource dataSource;
    private JdbcContextFactory jdbcContextFactory;
    private String tableInfoUtilsClass;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getTableInfoUtilsClass() {
        return tableInfoUtilsClass;
    }

    public void setTableInfoUtilsClass(String tableInfoUtilsClass) {
        this.tableInfoUtilsClass = tableInfoUtilsClass;
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
        Environment environment;
        if (StringUtils.isNullOrEmpty(tableInfoUtilsClass))
            environment = new Environment(new SpringTransactionFactory(), dataSource, this);
        else {
            try {
                Class<? extends TableInfoUtils> clazz = (Class<? extends TableInfoUtils>) Class.forName(tableInfoUtilsClass);
                TableInfoUtils tableInfoUtils = clazz.getConstructor().newInstance();
                environment = new Environment(new SpringTransactionFactory(), dataSource, this, tableInfoUtils);
            } catch (ClassNotFoundException | ClassCastException | NoSuchMethodException | InstantiationException |
                     IllegalAccessException | InvocationTargetException e) {
                environment = new Environment(new SpringTransactionFactory(), dataSource, this);
                logger.error("The tableInfoUtils class not found or not cast.\t\nmessage:" + e.getMessage() + "\t\ncause:" + e.getCause());
            }
        }
        this.jdbcContextFactory = new SimpleContextFactory(environment);
        ExecutorUtils.proxyAllExecutor(this.jdbcContextFactory);
    }

    @Override
    public JdbcContextFactory getObject() {
        if (this.jdbcContextFactory == null)
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
