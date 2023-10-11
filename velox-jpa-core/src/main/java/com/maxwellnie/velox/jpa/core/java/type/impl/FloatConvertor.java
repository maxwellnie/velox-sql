package com.maxwellnie.velox.jpa.core.java.type.impl;

import com.maxwellnie.velox.jpa.core.java.type.TypeConvertor;

/**
 * @author Maxwell Nie
 */
public class FloatConvertor implements TypeConvertor<Float> {
    @Override
    public Float convert(Object original) {
        if (original == null)
            return 0.0f;
        if (original instanceof Number)
            return ((Number) original).floatValue();
        else
            return 0.0f;
    }
}
