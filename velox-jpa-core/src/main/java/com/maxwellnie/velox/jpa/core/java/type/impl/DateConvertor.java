package com.maxwellnie.velox.jpa.core.java.type.impl;

import com.maxwellnie.velox.jpa.core.exception.TypeNotEqualsException;
import com.maxwellnie.velox.jpa.core.java.type.TypeConvertor;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Maxwell Nie
 */
public class DateConvertor implements TypeConvertor<Date> {
    @Override
    public Date convert(Object original) {
        if (original == null)
            return null;
        else if (original instanceof Time){
            throw new TypeNotEqualsException("You want to get type of java.util.Date,but "+original+" is type of Time");
        }else if(original instanceof Timestamp){
            return new Date(((Timestamp) original).getTime());
        }else if(original instanceof java.sql.Date){
            return new Date(((java.sql.Date) original).getTime());
        }else
            throw new TypeNotEqualsException("You want to get type of java.util.Date,but "+original+" is "+original.getClass().getName());
    }
}
