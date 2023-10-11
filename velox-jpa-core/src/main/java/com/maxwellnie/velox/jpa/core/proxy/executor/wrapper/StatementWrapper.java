package com.maxwellnie.velox.jpa.core.proxy.executor.wrapper;

import com.maxwellnie.velox.jpa.core.utils.reflect.meta.MetaData;

import java.sql.Statement;

/**
 * @author Maxwell Nie
 */
public class StatementWrapper {
    private Statement statement;
    private MetaData metaData;

    public StatementWrapper(Statement statement, MetaData metaData) {
        this.statement = statement;
        this.metaData = metaData;
    }

    public StatementWrapper(Statement statement) {
        this.statement = statement;
        this.metaData = MetaData.ofEmpty();
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    public Object getProperty(String name) {
        return this.metaData.getProperty(name);
    }
}
