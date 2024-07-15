package com.maxwellnie.velox.sql.core.natives.stream;

/**
 * @author Maxwell Nie
 */
public interface ActionSet<T, I> {
    void accept(T t);

    void add(I action);

    void clear();

    void remove(I action);
}
