package com.maxwellnie.velox.sql.core.utils.jdbc;

import com.maxwellnie.velox.sql.core.natives.jdbc.datasource.DataSourceManager;

import javax.sql.DataSource;

/**
 * @author Maxwell Nie
 */
public class CurrentThreadUtils {
    private static final ThreadLocal<String> dataSourceName = new ThreadLocal<>();

    public static String getDataSourceName() {
        return dataSourceName.get();
    }

    public static void setDataSourceName(String name) {
        dataSourceName.set(name);
    }

    public static void clearDataSourceName() {
        dataSourceName.remove();
    }

    public static DataSource getDataSource() {
        return DataSourceManager.getDataSource(getDataSourceName());
    }


}
