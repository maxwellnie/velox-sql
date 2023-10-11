package com.maxwellnie.velox.jpa.core.java.type;

/**
 * @author Maxwell Nie
 */
public interface TypeConvertor<T> {
    public T convert(Object original);
}
