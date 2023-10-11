package com.maxwellnie.velox.jpa.core.cahce;

import java.util.Collection;
import java.util.Set;

public interface Cache<K, V> {
    void put(K k, V v);

    V get(K k);

    V remove(K k);

    int size();

    Set<K> keys();

    Collection<V> values();

    void clear();
}
