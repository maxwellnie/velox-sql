package com.crazy.sql.core.cahce;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface Cache<K,V> {
    void put(K k,V v);
    V get(K k);
    V remove(K k);
    int size();
    Set<K> keys();
    Collection<V> values();
    void clear();
}
