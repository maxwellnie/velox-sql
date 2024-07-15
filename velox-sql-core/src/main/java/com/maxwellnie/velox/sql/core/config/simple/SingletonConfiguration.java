package com.maxwellnie.velox.sql.core.config.simple;


import com.maxwellnie.velox.sql.core.config.Configuration;

/**
 * 单例模式，饿汉式，全局唯一配置，并不应用于与其他框架整合。
 *
 * @author Maxwell Nie
 */
public class SingletonConfiguration extends Configuration {
    private static final SingletonConfiguration INSTANCE = new SingletonConfiguration();

    private SingletonConfiguration() {

    }

    public static SingletonConfiguration getInstance() {
        return INSTANCE;
    }
}
