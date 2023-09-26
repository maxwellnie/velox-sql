package com.crazy.sql.core.cahce.key;

import java.util.Objects;

public class CacheKey {
    private Class<?> clazz;
    private String sql;
    private String accessorHashCode;

    public CacheKey(Class<?> clazz, String sql, String accessorHashCode) {
        this.clazz = clazz;
        this.sql = sql;
        this.accessorHashCode = accessorHashCode;
    }

    public String getAccessorHashCode() {
        return accessorHashCode;
    }

    public void setAccessorHashCode(String accessorHashCode) {
        this.accessorHashCode = accessorHashCode;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheKey key = (CacheKey) o;
        return Objects.equals(clazz, key.clazz) && Objects.equals(sql, key.sql) && Objects.equals(accessorHashCode, key.accessorHashCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, sql, accessorHashCode);
    }

    @Override
    public String toString(){
        return clazz.getName()+":"+accessorHashCode+":"+sql;
    }
}
