package com.crazy.sql.core.proxy.executor.exceptional.query;

import java.util.List;

/**
 * @author Akiba no ichiichiyoha
 */
public interface QueryHandle<E> {
    void handle(List<E> list);
}
