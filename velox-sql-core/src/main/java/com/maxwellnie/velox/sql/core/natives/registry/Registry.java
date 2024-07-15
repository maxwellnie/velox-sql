package com.maxwellnie.velox.sql.core.natives.registry;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Maxwell Nie
 */
public final class Registry {
    public static final Registry INSTANCE = new Registry();
    private final Map<Object, Object> REGISTRY = new LinkedHashMap<>();

    public <T> T getValue(Object key) {
        return (T) REGISTRY.get(key);
    }

    public void register(Object key, Object value) {
        REGISTRY.put(key, value);
    }
}
