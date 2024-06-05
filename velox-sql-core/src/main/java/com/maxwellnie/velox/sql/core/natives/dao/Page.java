package com.maxwellnie.velox.sql.core.natives.dao;

import java.util.List;

/**
 * @author Maxwell Nie
 */
public interface Page<T> {
    List<T> getResult();
    long getTotal();
    void setTotal(long total);
    long getCurrent();
    void setCurrent(long current);
    long getOffset();
    void setOffset(long offset);
}
