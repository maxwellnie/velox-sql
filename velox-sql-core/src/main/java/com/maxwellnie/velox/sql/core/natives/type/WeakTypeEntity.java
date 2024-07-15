package com.maxwellnie.velox.sql.core.natives.type;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Maxwell Nie
 */
public class WeakTypeEntity {
    private final Map<String, WeakTypeEntity> PROPERTIES = new LinkedHashMap<>();
    private final Class<?> targetType;
    private Object value;

    public WeakTypeEntity(Class<?> targetType) {
        this.targetType = targetType;
    }

    public static Builder create(Class<?> targetType) {
        return new Builder(targetType);
    }

    public void addProperty(String name, WeakTypeEntity weakTypeEntity) {
        PROPERTIES.put(name, weakTypeEntity);
    }

    public WeakTypeEntity getProperty(String name) {
        return PROPERTIES.get(name);
    }

    public Map<String, WeakTypeEntity> getProperties() {
        return PROPERTIES;
    }

    public void removeProperty(String name) {
        PROPERTIES.remove(name);
    }

    public Class<?> getTargetType() {
        return targetType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public static class Builder {
        private final Map<String, WeakTypeEntity> DATA = new HashMap<>();
        private Class<?> targetType;

        public Builder(Class<?> targetType) {
            this.targetType = targetType;
        }

        public Builder addProperty(String name, WeakTypeEntity weakTypeEntity) {
            DATA.put(name, weakTypeEntity);
            return this;
        }

        public Builder addProperty(String name) {
            DATA.put(name, null);
            return this;
        }

        public Builder removeProperty(String name) {
            DATA.remove(name);
            return this;
        }

        public WeakTypeEntity build() {
            WeakTypeEntity weakTypeEntity = new WeakTypeEntity(targetType);
            weakTypeEntity.PROPERTIES.putAll(DATA);
            return weakTypeEntity;
        }
    }
}
