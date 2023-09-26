package com.crazy.sql.spring.boot.configuration;

import com.crazy.sql.core.config.GlobalConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * 框架配置类
 * @author Akiba no ichiichiyoha
 */
@Component
@ConfigurationProperties(prefix = "crazy-sql")
public class CrazySQLConfiguration {
    /**
     * 和全局配置绑定
     */
    @NestedConfigurationProperty
    private GlobalConfig config= GlobalConfig.getInstance();
}
