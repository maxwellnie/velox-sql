package com.maxwellnie.velox.sql.core.natives.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public abstract class BaseSql {
    protected String sql;
    protected List<Object> params = new java.util.ArrayList<>();
    protected boolean isInject;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    public boolean isInject() {
        return isInject;
    }

    public void setInject(boolean inject) {
        isInject = inject;
    }
    public void addParam(Object value) {
        params.add(value);
    }
    public void addParams(Collection<Object> values) {
        params.addAll(values);
    }
    public void addParams(Object[] values) {
        Collections.addAll(params, values);
    }
}
