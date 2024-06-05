package com.maxwellnie.velox.sql.core.natives.wrapper;

import com.maxwellnie.velox.sql.core.meta.MetaData;

/**
 * 元数据风格包装器
 * @author Maxwell Nie
 */
public abstract class MetaStyleWrapper<T> implements Wrapper<T>{
    /**
     * 包装的对象
     */
    protected T t;
    /**
     * 元数据
     */
    protected final MetaData meta = MetaData.ofEmpty();
    /**
     * 添加元数据
     * @param key
     * @param value
     */
    public void addProperty(String key, Object value) {
        meta.addProperty(key, value);
    }
    /**
     * 获取元数据
     * @param key
     * @return
     */
    public Object getProperty(String key) {
        return meta.getProperty(key);
    }
    @Override
    public T get() {
        return this.t;
    }

    @Override
    public void set(T t) {
        this.t = t;
    }
}
