package com.maxwellnie.velox.sql.spring.boot;

import com.maxwellnie.velox.sql.core.config.Configuration;
import com.maxwellnie.velox.sql.core.config.simple.SingletonConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author Maxwell Nie
 */
@ConfigurationProperties(prefix = "velox-sql")
public class VeloxSqlBootConfiguration {
    private String tableInfoManagerClass;
    @NestedConfigurationProperty
    private Configuration global = SingletonConfiguration.getInstance();
    public String getTableInfoManagerClass() {
        return tableInfoManagerClass;
    }

    public Configuration getGlobal() {
        return global;
    }

    public void setGlobal(Configuration configuration) {
        this.global = configuration;
    }

    public void setTableInfoManagerClass(String tableInfoManagerClass) {
        this.tableInfoManagerClass = tableInfoManagerClass;
    }
}
