package com.crazy.sql.core.java.type.impl;

import com.crazy.sql.core.java.type.TypeConvertor;

/**
 * @author Akiba no ichiichiyoha
 */
public class DoubleConvertor implements TypeConvertor<Double> {
    @Override
    public Double convert(Object original) {
        if(original==null)
            return 0.0D;
        if (original instanceof Number)
            return ((Number) original).doubleValue();
        else
            return 0.0D;
    }
}
