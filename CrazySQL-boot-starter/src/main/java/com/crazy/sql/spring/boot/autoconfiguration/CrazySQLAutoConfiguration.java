package com.crazy.sql.spring.boot.autoconfiguration;

import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.config.CrazySQLConfig;
import com.crazy.sql.core.executor.impl.NotCallBackSimpleSQLExecutor;
import com.crazy.sql.core.factory.AccessorFactory;
import com.crazy.sql.core.factory.impl.StandAccessorFactory;
import com.crazy.sql.spring.boot.configuration.CrazySQLConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConditionalOnClass({NotCallBackSimpleSQLExecutor.class, StandAccessorFactory.class})
@ConditionalOnSingleCandidate(DataSource.class)
@EnableConfigurationProperties(CrazySQLConfiguration.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
public class CrazySQLAutoConfiguration implements InitializingBean {

    private static final CrazySQLConfig config= CrazySQLConfig.getInstance();
    @Override
    public void afterPropertiesSet() throws Exception {

    }
    @Bean
    @ConditionalOnMissingBean
    public AccessorFactory accessorFactory(DataSource dataSource, CrazySQLConfiguration configuration){
        config.setTableSuffix(configuration.getTableSuffix());
        config.setMaximum(configuration.getMaximum());
        config.setStandColumn(configuration.isStandColumn());
        AccessorFactory accessorFactory=new StandAccessorFactory(dataSource,false);
        return accessorFactory;
    }
}
