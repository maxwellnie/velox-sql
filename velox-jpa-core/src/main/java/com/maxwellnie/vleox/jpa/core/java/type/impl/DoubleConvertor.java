package com.maxwellnie.vleox.jpa.core.java.type.impl;

import com.maxwellnie.vleox.jpa.core.java.type.TypeConvertor;

/**
 * @author Maxwell Nie
 */
public class DoubleConvertor implements TypeConvertor<Double> {
    @Override
    public Double convert(Object original) {
        if (original == null)
            return 0.0D;
        if (original instanceof Number)
            return ((Number) original).doubleValue();
        else
            return 0.0D;
    }
}
