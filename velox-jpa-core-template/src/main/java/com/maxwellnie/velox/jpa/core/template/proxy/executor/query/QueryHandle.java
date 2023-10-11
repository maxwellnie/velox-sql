package com.maxwellnie.velox.jpa.core.template.proxy.executor.query;

import java.util.List;

/**
 * @author Maxwell Nie
 */
public interface QueryHandle<E> {
    void handle(List<E> list);
}
