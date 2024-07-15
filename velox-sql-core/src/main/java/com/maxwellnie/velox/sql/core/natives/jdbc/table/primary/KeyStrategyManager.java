package com.maxwellnie.velox.sql.core.natives.jdbc.table.primary;

import com.maxwellnie.velox.sql.core.natives.jdbc.table.primary.generator.NoKeyGenerator;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.primary.keyselector.JdbcSelector;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.primary.keyselector.NoKeySelector;
import com.maxwellnie.velox.sql.core.natives.registry.Registry;

/**
 * 主键策略管理器，用于注册主键生成策略和主键获取策略
 * 请注意！这个主键策略管理器是局部线程安全的，建议在框架初始化前将主键设置完毕。
 *
 * @author Maxwell Nie
 */
public class KeyStrategyManager {
    public static final String REGISTRY_NAME = "velox-sql:primary:key:strategy:";
    /**
     * 默认的主键策略，无生成器，无获取器
     */
    public static final String DEFAULT = "default";
    /**
     * JDBC自增主键策略，无生成器，JdbcApi的主键获取器
     */
    public static final String JDBC_AUTO = "jdbc_auto";
    private static final Registry REGISTRY = Registry.INSTANCE;

    static {
        registerGenerator(DEFAULT, new PrimaryKeyStrategy(new NoKeyGenerator(), new NoKeySelector()));
        registerGenerator(JDBC_AUTO, new PrimaryKeyStrategy(new NoKeyGenerator(), new JdbcSelector()));
    }

    /**
     * 获取对应名字的主键策略
     *
     * @param name
     * @return
     */
    public static PrimaryKeyStrategy getPrimaryKeyStrategy(String name) {
        return REGISTRY.getValue(REGISTRY_NAME + name);
    }

    /**
     * 注册主键策略
     *
     * @param name
     * @param primaryKeyStrategy
     */
    public synchronized static void registerGenerator(String name, PrimaryKeyStrategy primaryKeyStrategy) {
        REGISTRY.register(REGISTRY_NAME + name, primaryKeyStrategy);
    }
}
