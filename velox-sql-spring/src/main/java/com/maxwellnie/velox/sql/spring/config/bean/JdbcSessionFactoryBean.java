package com.maxwellnie.velox.sql.spring.config.bean;

import com.maxwellnie.velox.sql.core.cache.Cache;
import com.maxwellnie.velox.sql.core.config.Configuration;
import com.maxwellnie.velox.sql.core.config.simple.SingletonConfiguration;
import com.maxwellnie.velox.sql.core.distributed.TransactionTask;
import com.maxwellnie.velox.sql.core.natives.exception.VeloxImplConfigException;
import com.maxwellnie.velox.sql.core.natives.jdbc.context.Context;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSessionFactory;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.impl.DefaultSessionFactory;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfoManager;
import com.maxwellnie.velox.sql.core.natives.jdbc.transaction.TransactionFactory;
import com.maxwellnie.velox.sql.core.utils.java.StringUtils;
import com.maxwellnie.velox.sql.spring.Version;
import com.maxwellnie.velox.sql.spring.listener.event.ContextCreationEvent;
import com.maxwellnie.velox.sql.spring.listener.event.PostContextCreationEvent;
import com.maxwellnie.velox.sql.spring.listener.event.PostJdbcSessionFactoryEvent;
import com.maxwellnie.velox.sql.spring.listener.event.SupportEvent;
import com.maxwellnie.velox.sql.spring.transaction.SpringTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;

/**
 * 配置类，可以通过设置来初始化JdbcSessionFactory
 *
 * @author Maxwell Nie
 */
public class JdbcSessionFactoryBean extends SpringStyleConfiguration implements InitializingBean, FactoryBean<JdbcSessionFactory>, ApplicationEventPublisherAware {
    private static final Logger logger = LoggerFactory.getLogger(JdbcSessionFactoryBean.class);
    private DataSource dataSource;
    private JdbcSessionFactory jdbcSessionFactory;
    private String tableInfoManagerClassName;
    private ApplicationEventPublisher applicationEventPublisher;
    private Configuration configuration;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getTableInfoManagerClassName() {
        return tableInfoManagerClassName;
    }

    public void setTableInfoManagerClassName(String tableInfoManagerClassName) {
        this.tableInfoManagerClassName = tableInfoManagerClassName;
    }

    private void initConfiguration() {
        if (configuration != null)
            return;
        try {
            configuration = SingletonConfiguration.getInstance();
            configuration.setStandTable(isStandTable());
            configuration.setStandColumn(isStandColumn());
            configuration.setDaoImplClass(Class.forName(getDaoImplClassName()));
            configuration.setCache(isCache());
            configuration.setLevel(getLevel());
            configuration.setTablePrefix(getTablePrefix());
            configuration.setCacheClass((Class<? extends Cache>) Class.forName(getCacheClassName()));
            configuration.setDialect(getDialect());
            configuration.setIsTaskQueue(getIsTaskQueue());
            configuration.setTaskQueue(getTaskQueue());
            logger.debug(getTransactionTaskClassName());
            configuration.setTransactionTaskClass((Class<? extends TransactionTask>) Class.forName(getTransactionTaskClassName()));
        } catch (ClassNotFoundException e) {
            throw new VeloxImplConfigException(e);
        }
    }

    @Override
    public void afterPropertiesSet() {
        initConfiguration();
        if (dataSource == null) {
            logger.error("Your datasource is null.");
            throw new VeloxImplConfigException("The datasource must be not null.");
        }
        Context context;
        TransactionFactory transactionFactory = new SpringTransactionFactory(dataSource);
        pushEvent(new ContextCreationEvent(configuration, dataSource, transactionFactory));
        if (StringUtils.isNullOrEmpty(tableInfoManagerClassName)) {
            context = new Context(transactionFactory, configuration);
        } else {
            try {
                Class<? extends TableInfoManager> clazz = (Class<? extends TableInfoManager>) Class.forName(tableInfoManagerClassName);
                TableInfoManager tableInfoManager = clazz.getConstructor().newInstance();
                context = new Context(new SpringTransactionFactory(dataSource), configuration, tableInfoManager);
            } catch (ClassNotFoundException | ClassCastException | NoSuchMethodException | InstantiationException |
                     IllegalAccessException | InvocationTargetException e) {
                logger.error("The tableInfoUtils class not found or not cast.\t\nmessage:" + e.getMessage() + "\t\ncause:" + e.getCause());
                throw new VeloxImplConfigException("The tableInfoUtils class not found or not cast.\t\nmessage:" + e.getMessage() + "\t\ncause:" + e.getCause());
            }
        }
        pushEvent(new PostContextCreationEvent(context, dataSource, transactionFactory));
        this.jdbcSessionFactory = new DefaultSessionFactory(context);
        pushEvent(new PostJdbcSessionFactoryEvent(context, this.jdbcSessionFactory));
        System.out.println(
                ".    ,     |            ,---.     |    \n" +
                        "|    |,---.|    ,---.  ,`---.,---.|    \n" +
                        " \\  / |---'|    |   |><     ||   ||    \n" +
                        "  `'  `---'`---'`---'  ``---'`---|`---'\n" +
                        "                                 |     " + Version.VERSION);
    }

    @Override
    public JdbcSessionFactory getObject() {
        if (this.jdbcSessionFactory == null)
            afterPropertiesSet();
        return this.jdbcSessionFactory;
    }

    public JdbcSessionFactory getJdbcSessionFactory() {
        return jdbcSessionFactory;
    }

    public void setJdbcSessionFactory(JdbcSessionFactory jdbcSessionFactory) {
        this.jdbcSessionFactory = jdbcSessionFactory;
    }

    public void pushEvent(SupportEvent event) {
        if (this.applicationEventPublisher != null)
            this.applicationEventPublisher.publishEvent(event);
    }

    @Override
    public Class<?> getObjectType() {
        return JdbcSessionFactory.class;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
