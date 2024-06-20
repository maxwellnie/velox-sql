package com.maxwellnie.velox.sql.core.natives.jdbc.datasource;

import com.maxwellnie.velox.sql.core.natives.registry.Registry;

import javax.sql.DataSource;

/**
 * @author Maxwell Nie
 */
public class DataSourceManager {
    public static final String REGISTRY_NAME = "velox-sql:data-source:";
    private static final Registry REGISTRY = Registry.INSTANCE;
    public static DataSource getDataSource(String name) {
        return REGISTRY.getValue(REGISTRY_NAME + name);
    }
    public static void register(String name, DataSource dataSource) {
        REGISTRY.register(REGISTRY_NAME + name, dataSource);
    }
}
