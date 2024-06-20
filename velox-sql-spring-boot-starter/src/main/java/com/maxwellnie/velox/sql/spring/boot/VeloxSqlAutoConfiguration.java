package com.maxwellnie.velox.sql.spring.boot;

import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSession;
import com.maxwellnie.velox.sql.spring.config.bean.JdbcSessionFactoryBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author Maxwell Nie
 */
@Configuration
@ConditionalOnClass({DataSource.class, JdbcSession.class, JdbcSessionFactoryBean.class})
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties({VeloxSqlBootConfiguration.class})
public class VeloxSqlAutoConfiguration {
    private final VeloxSqlBootConfiguration veloxJpaBootConfig;

    public VeloxSqlAutoConfiguration(VeloxSqlBootConfiguration veloxJpaBootConfig) {
        this.veloxJpaBootConfig = veloxJpaBootConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    public JdbcSessionFactoryBean jdbcSessionFactoryBean(DataSource dataSource) {
        JdbcSessionFactoryBean jdbcSessionFactoryBean = new JdbcSessionFactoryBean();
        jdbcSessionFactoryBean.setDataSource(dataSource);
        jdbcSessionFactoryBean.setConfiguration(veloxJpaBootConfig.getGlobal());
        jdbcSessionFactoryBean.setTableInfoUtilsClass(veloxJpaBootConfig.getTableInfoManagerClass());
        return jdbcSessionFactoryBean;
    }
}
