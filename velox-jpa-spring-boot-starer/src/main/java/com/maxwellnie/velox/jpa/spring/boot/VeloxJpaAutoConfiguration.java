package com.maxwellnie.velox.jpa.spring.boot;

import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContext;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author Maxwell Nie
 */
@Configuration
@EnableAutoConfiguration
@ConditionalOnClass({DataSource.class, JdbcContext.class})
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties({VeloxJpaBootConfig.class})
public class VeloxJpaAutoConfiguration {

}
