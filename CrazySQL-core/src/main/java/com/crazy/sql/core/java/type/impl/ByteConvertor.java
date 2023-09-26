package com.crazy.sql.core.java.type.impl;

import com.crazy.sql.core.java.type.TypeConvertor;

/**
 * @author Akiba no ichiichiyoha
 */
public class ByteConvertor implements TypeConvertor<Byte> {
    @Override
    public Byte convert(Object original) {
        if(original==null)
            return 0;
        if (original instanceof Number)
            return ((Number) original).byteValue();
        else
                return 0;
    }
}
