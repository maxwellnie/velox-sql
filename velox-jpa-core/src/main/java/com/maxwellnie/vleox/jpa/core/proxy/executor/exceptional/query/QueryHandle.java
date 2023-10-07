package com.maxwellnie.vleox.jpa.core.proxy.executor.exceptional.query;

import java.util.List;

/**
 * @author Maxwell Nie
 */
public interface QueryHandle<E> {
    void handle(List<E> list);
}
