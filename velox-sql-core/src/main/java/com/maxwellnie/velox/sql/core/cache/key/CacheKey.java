package com.maxwellnie.velox.sql.core.cache.key;

import com.maxwellnie.velox.sql.core.utils.base.CollectionUtils;

import javax.sql.DataSource;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

public class CacheKey implements Serializable {
    private static final long serialVersionUID = 12755633923021L;
    private final List<Object> values = Collections.synchronizedList(new LinkedList<>());
    private Class<?> clazz;
    private String sql;
    private DataSource dataSource;
    private String daoImplHashCode;

    public CacheKey(Class<?> clazz, String sql, DataSource dataSource, String daoImplHashCode) {
        this.clazz = clazz;
        this.sql = sql;
        this.daoImplHashCode = daoImplHashCode;
        this.dataSource = dataSource;
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

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 增加参数
     *
     * @param value 可以是数组，也可以是集合
     */
    public void addParams(Object value) {
        if (value == null)
            values.add(null);
        else if (value.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(value); i++) {
                values.add(Array.get(value, i));
            }
        } else if (CollectionUtils.isCollection(value)) {
            values.addAll((Collection<?>) value);
        } else
            values.add(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheKey key = (CacheKey) o;
        if (!(Objects.equals(clazz, key.clazz) && Objects.equals(sql, key.sql) && Objects.equals(daoImplHashCode, key.daoImplHashCode) && Objects.equals(dataSource, key.dataSource)))
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
        return Objects.hash(clazz, sql, daoImplHashCode, dataSource, values);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(":");
        joiner.add(clazz.getName())
                .add(daoImplHashCode)
                .add(dataSource.getClass().getName())
                .add(String.valueOf(dataSource.hashCode()))
                .add(sql);
        for (Object value : values)
            if (value != null)
                joiner.add(value.hashCode() + "");
            else
                joiner.add("null");
        return joiner.toString();
    }

}
