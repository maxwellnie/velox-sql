package com.crazy.sql.core.config;

import com.crazy.sql.core.cahce.manager.CacheManager;
import com.crazy.sql.core.cahce.manager.impl.SimpleCacheManager;

import java.util.Map;
import java.util.Properties;

/**
 * 单例模式，饿汉式，设置全局唯一配置
 */
public class SQLConfig {
    private String diverClassName;
    private String url;
    private String userName;
    private String password;
    private int maximum;
    private static final SQLConfig sqlConfig=new SQLConfig();
    private SQLConfig(){

    }
    public static SQLConfig getInstance(){
        return sqlConfig;
    }
    public static void setConfig(Properties properties) {
        sqlConfig.diverClassName = properties.getProperty("driverClassName");
        sqlConfig.url = properties.getProperty("url");
        sqlConfig.userName = properties.getProperty("userName");
        sqlConfig.password = properties.getProperty("password");
        sqlConfig.maximum = Integer.parseInt(properties.getProperty("maximum"));
    }
    public static void setConfig(Map<String,String> map) {
        sqlConfig.diverClassName = map.get("driverClassName");
        sqlConfig.url = map.get("url");
        sqlConfig.userName = map.get("userName");
        sqlConfig.password = map.get("password");
        sqlConfig.maximum = Integer.parseInt(map.get("maximum"));
    }
    public static void setConfig(String diverClassName, String url, String userName, String password, int maximum){
        sqlConfig.diverClassName = diverClassName;
        sqlConfig.url = url;
        sqlConfig.userName = userName;
        sqlConfig.password = password;
        sqlConfig.maximum = maximum;
    }
    public String getDiverClassName() {
        return diverClassName;
    }

    public void setDiverClassName(String diverClassName) {
        this.diverClassName = diverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    @Override
    public String toString() {
        return "SQLConfig{" +
                "diverClassName='" + diverClassName + '\'' +
                ", url='" + url + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", maximum=" + maximum +
                '}';
    }
}
