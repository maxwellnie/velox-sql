package com.maxwellnie.velox.jpa.core.cahce.key;

import com.maxwellnie.velox.jpa.core.exception.ClassTypeException;

import java.io.Serializable;
import java.util.*;

public class CacheKey implements Serializable {
    private static final long serialVersionUID = 12755633923021L;
    private final List<Object> values = Collections.synchronizedList(new LinkedList<>());
    private Class<?> clazz;
    private String sql;
    private String daoImplHashCode;

    public CacheKey(Class<?> clazz, String sql, String daoImplHashCode) {
        this.clazz = clazz;
        this.sql = sql;
        this.daoImplHashCode=daoImplHashCode;
    }

    public String getDaoImplHashCode() {
        return daoImplHashCode;
    }

    public void setDaoImplHashCode(String daoImplHashCode) {
        this.daoImplHashCode = daoImplHashCode;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public void addValue(Object value) {
        if (!value.getClass().isArray()) {
            this.values.add(value);
        } else
            throw new ClassTypeException("The added element must not be an array.if you need added all element of array,you need use 'addValueArray()'");
    }

    public void addValueArray(Object... values) {
        this.addValueCollection(Arrays.asList(values));
    }

    public void addValueCollection(Collection<Object> collection) {
        this.values.addAll(collection);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheKey key = (CacheKey) o;
        if (!(Objects.equals(clazz, key.clazz) && Objects.equals(sql, key.sql) && Objects.equals(daoImplHashCode, key.daoImplHashCode)))
            return false;
        if (this.values.size() != key.values.size())
            return false;
        for (int i = 0; i < values.size(); i++) {
            if (!values.get(i).equals(key.values.get(i)))
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, sql, daoImplHashCode);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(":");
        joiner.add(clazz.getName())
                .add(daoImplHashCode)
                .add(sql);
        for (Object value : values)
            if (value != null)
                joiner.add(value.toString());
            else
                joiner.add("null");
        return joiner.toString();
    }
}
