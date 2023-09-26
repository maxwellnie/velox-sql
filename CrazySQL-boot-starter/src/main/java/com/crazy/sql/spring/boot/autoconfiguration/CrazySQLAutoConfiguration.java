package com.crazy.sql.spring.boot.autoconfiguration;

import com.crazy.sql.spring.boot.configuration.CrazySQLConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
/**
 * 框架自动配置类
 * @author Akiba no ichiichiyoha
 */
@Configuration
@ConditionalOnSingleCandidate(DataSource.class)
@EnableConfigurationProperties(CrazySQLConfiguration.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
public class CrazySQLAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {

    }

}
