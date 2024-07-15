package com.maxwellnie.velox.sql.spring.boot;

import com.maxwellnie.velox.sql.core.config.Configuration;
import com.maxwellnie.velox.sql.core.config.simple.SingletonConfiguration;
import com.maxwellnie.velox.sql.spring.support.NoSpringTransactionTask;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author Maxwell Nie
 */
@ConfigurationProperties(prefix = "velox-sql")
public class VeloxSqlBootConfiguration {
    private String tableInfoManagerClass;
    @NestedConfigurationProperty
    private Configuration global = initConfiguration();

    public String getTableInfoManagerClass() {
        return tableInfoManagerClass;
    }

    private Configuration initConfiguration() {
        Configuration configuration = SingletonConfiguration.getInstance();
        configuration.setTransactionTaskClass(NoSpringTransactionTask.class);
        return configuration;
    }

    public void setTableInfoManagerClass(String tableInfoManagerClass) {
        this.tableInfoManagerClass = tableInfoManagerClass;
    }

    public Configuration getGlobal() {
        return global;
    }

    public void setGlobal(Configuration configuration) {
        this.global = configuration;
    }
}
