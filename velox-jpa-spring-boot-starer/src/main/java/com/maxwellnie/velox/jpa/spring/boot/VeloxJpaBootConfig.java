package com.maxwellnie.velox.jpa.spring.boot;

import com.maxwellnie.velox.jpa.core.config.BaseConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Maxwell Nie
 */
@ConfigurationProperties(prefix = "velox-jpa")
public class VeloxJpaBootConfig extends BaseConfig {
    private String tableInfoUtilsClass;

    public String getTableInfoUtilsClass() {
        return tableInfoUtilsClass;
    }

    public void setTableInfoUtilsClass(String tableInfoUtilsClass) {
        this.tableInfoUtilsClass = tableInfoUtilsClass;
    }
}
