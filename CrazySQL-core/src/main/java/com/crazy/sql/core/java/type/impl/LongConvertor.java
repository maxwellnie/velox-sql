package com.crazy.sql.core.java.type.impl;

import com.crazy.sql.core.java.type.TypeConvertor;

/**
 * @author Akiba no ichiichiyoha
 */
public class LongConvertor implements TypeConvertor<Long> {
    @Override
    public Long convert(Object original) {
        if(original==null)
            return 0L;
        if (original instanceof Number)
            return ((Number) original).longValue();
        else
            return 0L;
    }
}
