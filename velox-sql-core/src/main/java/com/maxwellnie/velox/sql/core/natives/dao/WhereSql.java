package com.maxwellnie.velox.sql.core.natives.dao;

import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlPool;

import java.util.Collection;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class WhereSql extends BaseSql {
    protected String sql = SqlPool.WHERE;
    protected boolean isInject = true;
    protected List<InlineSql> inlineSql = new java.util.ArrayList<>();

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public boolean isInject() {
        return isInject;
    }

    @Override
    public void setInject(boolean inject) {
        isInject = inject;
    }

    public List<InlineSql> getInlineSql() {
        return inlineSql;
    }

    public void setInlineSql(List<InlineSql> inlineSql) {
        this.inlineSql = inlineSql;
    }

    public void addInlineSql(InlineSql inlineSql) {
        this.inlineSql.add(inlineSql);
    }

    public void addInlineSql(Collection<InlineSql> inlineSql) {
        this.inlineSql.addAll(inlineSql);
    }

    public void addInlineSql(InlineSql[] inlineSql) {
        this.inlineSql.addAll(java.util.Arrays.asList(inlineSql));
    }

    public static class InlineSql extends BaseSql {
        boolean isRelationship;

        public InlineSql(boolean isRelationship) {
            this.isRelationship = isRelationship;
        }

        public InlineSql() {
        }

        public boolean isRelationship() {
            return isRelationship;
        }

        public void setRelationship(boolean relationship) {
            isRelationship = relationship;
        }
    }
}
