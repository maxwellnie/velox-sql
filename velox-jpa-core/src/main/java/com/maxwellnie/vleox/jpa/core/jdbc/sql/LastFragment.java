package com.maxwellnie.vleox.jpa.core.jdbc.sql;

import com.maxwellnie.vleox.jpa.core.utils.java.StringUtils;

/**
 * @author Maxwell Nie
 */
public class LastFragment implements SqlFragment {
    private String nativeSql;
    private Object[] values;

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    @Override
    public String getNativeSql() {
        return StringUtils.getNoNullStr(nativeSql);
    }

    public void setNativeSql(String nativeSql) {
        this.nativeSql = nativeSql;
    }
}
