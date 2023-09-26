package com.crazy.sql.core.java.type.impl;

import com.crazy.sql.core.java.type.TypeConvertor;

/**
 * @author Akiba no ichiichiyoha
 */
public class IntegerConvertor implements TypeConvertor<Integer> {
    @Override
    public Integer convert(Object original) {
        if(original==null)
            return 0;
        if (original instanceof Number)
            return ((Number) original).intValue();
        else
            return 0;
    }
}
