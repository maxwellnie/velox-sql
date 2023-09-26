package com.crazy.sql.core.jdbc.sql;

import com.crazy.sql.core.utils.java.StringUtils;

/**
 * @author Akiba no ichiichiyoha
 */
public class LastFragment implements SqlFragment {
    private String nativeSql;
    private Object[] values;

    public void setNativeSql(String nativeSql) {
        this.nativeSql = nativeSql;
    }

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
}
