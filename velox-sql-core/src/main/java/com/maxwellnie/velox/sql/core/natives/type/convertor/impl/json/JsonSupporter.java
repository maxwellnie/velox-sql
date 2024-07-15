package com.maxwellnie.velox.sql.core.natives.type.convertor.impl.json;

/**
 * @author Maxwell Nie
 */
public interface JsonSupporter {
    String toJson(Object object);

    <T> T fromJson(Class<T> clazz, String json);

    <T> T empty();
}
