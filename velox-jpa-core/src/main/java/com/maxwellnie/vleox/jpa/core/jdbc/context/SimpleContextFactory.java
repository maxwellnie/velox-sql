package com.maxwellnie.vleox.jpa.core.jdbc.context;

import com.maxwellnie.vleox.jpa.core.config.simple.VeloxJpaConfig;
import com.maxwellnie.vleox.jpa.core.dao.support.env.Environment;

/**
 * JdbcContext工厂，生产出一个JdbcContext实例
 *
 * @author Maxwell Nie
 */
public class SimpleContextFactory implements JdbcContextFactory {
    private final Environment environment;

    public SimpleContextFactory(Environment environment) {
        this.environment = environment;
    }

    @Override
    public JdbcContext produce() {
        return produce(false);
    }

    @Override
    public JdbcContext produce(boolean autoCommit) {
        return new SimpleContext(
                environment.
                        getTransactionFactory().
                        produce(environment.getDataSource(), autoCommit, VeloxJpaConfig.getInstance().getLevel()), autoCommit);
    }

    @Override
    public Environment getEnvironment() {
        return this.environment;
    }
}