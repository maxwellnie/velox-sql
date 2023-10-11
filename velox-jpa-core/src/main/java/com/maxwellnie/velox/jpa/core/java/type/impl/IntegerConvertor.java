package com.maxwellnie.velox.jpa.core.java.type.impl;

import com.maxwellnie.velox.jpa.core.java.type.TypeConvertor;

/**
 * @author Maxwell Nie
 */
public class IntegerConvertor implements TypeConvertor<Integer> {
    @Override
    public Integer convert(Object original) {
        if (original == null)
            return 0;
        if (original instanceof Number)
            return ((Number) original).intValue();
        else
            return 0;
    }
}
