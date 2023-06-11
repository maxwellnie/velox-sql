package com.crazy.sql.core.config;


import java.util.Properties;

/**
 * 单例模式，饿汉式，设置全局唯一配置
 */
public class CrazySQLConfig {
    private int maximum;
    private String tableSuffix="";
    private boolean standColumn=false;
    private Properties properties=new Properties();;
    private static final CrazySQLConfig CRAZY_SQL_CONFIG =new CrazySQLConfig();
    private CrazySQLConfig(){

    }
    public static CrazySQLConfig getInstance(){
        return CRAZY_SQL_CONFIG;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public String getTableSuffix() {
        return tableSuffix;
    }

    public void setTableSuffix(String tableSuffix) {
        this.tableSuffix = tableSuffix;
    }

    public boolean isStandColumn() {
        return standColumn;
    }

    public void setStandColumn(boolean standColumn) {
        this.standColumn = standColumn;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Properties toProperties(){
        properties.put("maximum",maximum);
        properties.put("tableSuffix",tableSuffix);
        properties.put("standColumn",standColumn);
        return properties;
    }

    @Override
    public String toString() {
        return "CrazySQLConfig{" +
                ", maximum=" + maximum +
                ", tableSuffix='" + tableSuffix + '\'' +
                ", standColumn=" + standColumn +
                '}';
    }
}
