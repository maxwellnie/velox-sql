package com.maxwellnie.velox.jpa.core.java.type.impl;

import com.maxwellnie.velox.jpa.core.java.type.TypeConvertor;

/**
 * @author Maxwell Nie
 */
public class BooleanConvertor implements TypeConvertor<Boolean> {
    @Override
    public Boolean convert(Object original) {
        if (original == null)
            return false;
        if (original instanceof Number) {
            return ((Number) original).doubleValue() > 0;
        } else if (original instanceof Boolean) {
            return (Boolean) original;
        } else {
            try {
                return Boolean.valueOf(original.toString());
            } catch (NumberFormatException e) {
                return false; // 转换失败，返回默认值
            }
        }
    }
}
