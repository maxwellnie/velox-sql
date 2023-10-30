package com.maxwellnie.velox.jpa.core.manager;

import com.maxwellnie.velox.jpa.core.jdbc.table.primary.PrimaryKeyStrategy;
import com.maxwellnie.velox.jpa.core.jdbc.table.primary.generator.NoKeyGenerator;
import com.maxwellnie.velox.jpa.core.jdbc.table.primary.keyselector.JdbcSelector;
import com.maxwellnie.velox.jpa.core.jdbc.table.primary.keyselector.NoKeySelector;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 主键策略管理器，用于注册主键生成策略和主键获取策略
 *
 * @author Maxwell Nie
 */
public class KeyStrategyManager {
    /**
     * 默认的主键策略，无生成器，无获取器
     */
    public static final String DEFAULT = "default";
    /**
     * JDBC自增主键策略，无生成器，JdbcApi的主键获取器
     */
    public static final String JDBC_AUTO = "jdbc_auto";
    private static final Map<String, PrimaryKeyStrategy> primaryKeyStrategyMap = Collections.synchronizedMap(new LinkedHashMap<>());

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
        return primaryKeyStrategyMap.get(name);
    }

    /**
     * 注册主键策略
     *
     * @param name
     * @param primaryKeyStrategy
     */
    public static void registerGenerator(String name, PrimaryKeyStrategy primaryKeyStrategy) {
        primaryKeyStrategyMap.put(name, primaryKeyStrategy);
    }
}
