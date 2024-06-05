package com.maxwellnie.velox.sql.core.natives.dao;

import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlPool;

import java.util.List;

/**
 * @author Maxwell Nie
 */
public class GroupBySql extends BaseSql{
    protected String sql = SqlPool.GROUP_BY;
    protected boolean isInject = false;

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
}
