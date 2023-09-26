package com.crazy.sql.core.java.type.impl;

import com.crazy.sql.core.java.type.TypeConvertor;

/**
 * @author Akiba no ichiichiyoha
 */
public class ShortConvertor implements TypeConvertor<Short> {
    @Override
    public Short convert(Object original) {
        if(original==null)
            return 0;
        if (original instanceof Number)
            return ((Number) original).shortValue();
        else
            return 0;
    }
}
