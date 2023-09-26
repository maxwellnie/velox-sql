package com.crazy.sql.core.java.type.impl;

import com.crazy.sql.core.java.type.TypeConvertor;

/**
 * @author Akiba no ichiichiyoha
 */
public class DefaultConvertor implements TypeConvertor {
    @Override
    public Object convert(Object original) {
        return original;
    }
}
