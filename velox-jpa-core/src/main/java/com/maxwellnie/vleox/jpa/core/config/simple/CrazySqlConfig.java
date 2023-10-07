package com.maxwellnie.vleox.jpa.core.config.simple;


import com.maxwellnie.vleox.jpa.core.config.BaseConfig;

/**
 * 单例模式，饿汉式，设置全局唯一配置
 *
 * @author Maxwell Nie
 */
public class CrazySqlConfig extends BaseConfig {
    private static final CrazySqlConfig INSTANCE = new CrazySqlConfig();

    private CrazySqlConfig() {

    }

    public static CrazySqlConfig getInstance() {
        return INSTANCE;
    }
}
