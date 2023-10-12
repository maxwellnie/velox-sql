package com.maxwellnie.velox.jpa.spring.boot;

import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContext;
import com.velox.jpa.spring.config.bean.JdbcContextFactoryBean;
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
@ConditionalOnClass({DataSource.class, JdbcContext.class, JdbcContextFactoryBean.class})
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties({VeloxJpaBootConfig.class})
public class VeloxJpaAutoConfiguration {
    private final VeloxJpaBootConfig veloxJpaBootConfig;

    public VeloxJpaAutoConfiguration(VeloxJpaBootConfig veloxJpaBootConfig) {
        this.veloxJpaBootConfig = veloxJpaBootConfig;
    }
    @Bean
    @ConditionalOnMissingBean
    public JdbcContextFactoryBean jdbcContextFactoryBean(DataSource dataSource){
        JdbcContextFactoryBean jdbcContextFactoryBean=new JdbcContextFactoryBean();
        jdbcContextFactoryBean.setDataSource(dataSource);
        jdbcContextFactoryBean.setCache(veloxJpaBootConfig.isCache());
        jdbcContextFactoryBean.setCacheClassName(veloxJpaBootConfig.getCacheClassName());
        jdbcContextFactoryBean.setLevel(veloxJpaBootConfig.getLevel());
        jdbcContextFactoryBean.setStandColumn(veloxJpaBootConfig.isStandColumn());
        jdbcContextFactoryBean.setStandTable(veloxJpaBootConfig.isStandColumn());
        jdbcContextFactoryBean.setTablePrefix(veloxJpaBootConfig.getTablePrefix());
        return jdbcContextFactoryBean;
    }
}
