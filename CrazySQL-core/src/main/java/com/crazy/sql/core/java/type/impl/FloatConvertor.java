package com.crazy.sql.core.java.type.impl;

import com.crazy.sql.core.java.type.TypeConvertor;

/**
 * @author Akiba no ichiichiyoha
 */
public class FloatConvertor implements TypeConvertor<Float> {
    @Override
    public Float convert(Object original) {
        if(original==null)
            return 0.0f;
        if (original instanceof Number)
            return ((Number) original).floatValue();
        else
            return 0.0f;
    }
}
