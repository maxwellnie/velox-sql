package com.maxwellnie.velox.jpa.core.java.type.impl;

import com.maxwellnie.velox.jpa.core.java.type.TypeConvertor;

/**
 * @author Maxwell Nie
 */
public class ByteConvertor implements TypeConvertor<Byte> {
    @Override
    public Byte convert(Object original) {
        if (original == null)
            return 0;
        if (original instanceof Number)
            return ((Number) original).byteValue();
        else
            return 0;
    }
}
