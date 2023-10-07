package com.maxwellnie.vleox.jpa.core.java.type.impl;

import com.maxwellnie.vleox.jpa.core.java.type.TypeConvertor;

/**
 * @author Maxwell Nie
 */
public class BooleanConvertor implements TypeConvertor<Boolean> {
    @Override
    public Boolean convert(Object original) {
        if (original == null)
            return false;
        double value;
        if (original instanceof Number) {
            value = ((Number) original).doubleValue();
        } else if (original instanceof Boolean) {
            value = ((Boolean) original) ? 1 : 0;
        } else {
            try {
                value = Double.parseDouble(original.toString());
            } catch (NumberFormatException e) {
                return false; // 转换失败，返回默认值
            }
        }

        return value > 0.0;
    }
}
