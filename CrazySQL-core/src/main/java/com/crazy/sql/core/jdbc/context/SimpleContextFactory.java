package com.crazy.sql.core.jdbc.context;

import com.crazy.sql.core.accessor.env.Environment;
import com.crazy.sql.core.config.GlobalConfig;

/**
 * @author Akiba no ichiichiyoha
 */
public class SimpleContextFactory implements JdbcContextFactory{
    private Environment environment;

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
                produce(environment.getDataSource(), false, GlobalConfig.getInstance().getLevel()), autoCommit);
    }
}
