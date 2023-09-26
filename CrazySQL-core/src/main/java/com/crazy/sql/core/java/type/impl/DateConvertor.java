package com.crazy.sql.core.java.type.impl;

import com.crazy.sql.core.java.type.TypeConvertor;

import java.util.Date;

/**
 * @author Akiba no ichiichiyoha
 */
public class DateConvertor implements TypeConvertor<Date> {
    @Override
    public Date convert(Object original) {
        if(original==null)
            return null;
        else
            return new Date(((Date)original).getTime());
    }
}
