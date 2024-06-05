package com.maxwellnie.velox.sql.core.natives.dao;

import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlPool;

import java.util.Collection;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class HavingSql extends BaseSql{
    protected String sql = SqlPool.HAVING;
    protected boolean isInject = true;
    protected List<WhereSql.InlineSql> inlineSql = new java.util.ArrayList<>();
    public List<WhereSql.InlineSql> getInlineSql() {
        return inlineSql;
    }

    public void setInlineSql(List<WhereSql.InlineSql> inlineSql) {
        this.inlineSql = inlineSql;
    }
    public void addInlineSql(WhereSql.InlineSql inlineSql) {
        this.inlineSql.add(inlineSql);
    }
    public void addInlineSql(Collection<WhereSql.InlineSql> inlineSql) {
        this.inlineSql.addAll(inlineSql);
    }
    public void addInlineSql(WhereSql.InlineSql[] inlineSql) {
        this.inlineSql.addAll(java.util.Arrays.asList(inlineSql));
    }
}
