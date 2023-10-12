package com.maxwellnie.velox.jpa.spring.boot;

import com.maxwellnie.velox.jpa.core.config.BaseConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Maxwell Nie
 */
@ConfigurationProperties(prefix = "velox-jpa")
public class VeloxJpaBootConfig extends BaseConfig {
}
