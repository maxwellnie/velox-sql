package com.maxwellnie.velox.jpa.core.java.type.impl;

import com.maxwellnie.velox.jpa.core.java.type.TypeConvertor;

/**
 * @author Maxwell Nie
 */
public class ShortConvertor implements TypeConvertor<Short> {
    @Override
    public Short convert(Object original) {
        if (original == null)
            return 0;
        if (original instanceof Number)
            return ((Number) original).shortValue();
        else
            return 0;
    }
}
