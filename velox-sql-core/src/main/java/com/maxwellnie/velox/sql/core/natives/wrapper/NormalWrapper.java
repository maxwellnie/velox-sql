package com.maxwellnie.velox.sql.core.natives.wrapper;

/**
 * 常规包装器
 * @author Maxwell Nie
 */
public abstract class NormalWrapper<T> implements Wrapper<T>{
    protected T t;
    @Override
    public T get() {
        return this.t;
    }

    @Override
    public void set(T t) {
        this.t = t;
    }
}
