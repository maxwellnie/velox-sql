package com.maxwellnie.vleox.jpa.core.java.type.impl;

import com.maxwellnie.vleox.jpa.core.java.type.TypeConvertor;

/**
 * @author Maxwell Nie
 */
public class LongConvertor implements TypeConvertor<Long> {
    @Override
    public Long convert(Object original) {
        if (original == null)
            return 0L;
        if (original instanceof Number)
            return ((Number) original).longValue();
        else
            return 0L;
    }
}
