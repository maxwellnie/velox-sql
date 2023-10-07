package com.maxwellnie.vleox.jpa.core.java.type.impl;

import com.maxwellnie.vleox.jpa.core.java.type.TypeConvertor;

import java.util.Date;

/**
 * @author Maxwell Nie
 */
public class DateConvertor implements TypeConvertor<Date> {
    @Override
    public Date convert(Object original) {
        if (original == null)
            return null;
        else
            return new Date(((Date) original).getTime());
    }
}
