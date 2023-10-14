package com.maxwellnie.velox.jpa.core.template.sql;

import com.maxwellnie.velox.jpa.core.jdbc.sql.SqlFragment;
import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;

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
