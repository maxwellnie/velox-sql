package com.maxwellnie.velox.jpa.core.utils;

import java.util.Collection;

/**
 * @author Maxwell Nie
 */
public class CollectionUtils {
    public static Class<?>[] toClassArray(Collection<Class<?>> collection) {
        assert collection != null : "collection is null!!";
        return collection.toArray(new Class<?>[0]);
    }
}
