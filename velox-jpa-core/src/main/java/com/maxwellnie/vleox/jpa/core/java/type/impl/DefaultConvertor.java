package com.maxwellnie.vleox.jpa.core.java.type.impl;

import com.maxwellnie.vleox.jpa.core.java.type.TypeConvertor;

/**
 * @author Maxwell Nie
 */
public class DefaultConvertor implements TypeConvertor {
    @Override
    public Object convert(Object original) {
        return original;
    }
}
