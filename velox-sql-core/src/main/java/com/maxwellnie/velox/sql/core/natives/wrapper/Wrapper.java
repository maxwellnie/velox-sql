package com.maxwellnie.velox.sql.core.natives.wrapper;

/**
 * 在框架不同组件种传递数据时，由于框架是高度自定义的，所以需要一个可以传递数据的统一接口。
 *
 * @author Maxwell Nie
 */
public interface Wrapper<T> {
    /**
     * 获取数据
     *
     * @return
     */
    T get();

    /**
     * 设置数据
     *
     * @param t
     */
    void set(T t);
}
