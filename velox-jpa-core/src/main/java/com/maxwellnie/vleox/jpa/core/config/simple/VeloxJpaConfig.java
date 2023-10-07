package com.maxwellnie.vleox.jpa.core.config.simple;


import com.maxwellnie.vleox.jpa.core.config.BaseConfig;

/**
 * 单例模式，饿汉式，设置全局唯一配置
 *
 * @author Maxwell Nie
 */
public class VeloxJpaConfig extends BaseConfig {
    private static final VeloxJpaConfig INSTANCE = new VeloxJpaConfig();

    private VeloxJpaConfig() {

    }

    public static VeloxJpaConfig getInstance() {
        return INSTANCE;
    }
}
