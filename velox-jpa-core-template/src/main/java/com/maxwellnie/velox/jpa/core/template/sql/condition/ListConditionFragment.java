package com.maxwellnie.velox.jpa.core.template.sql.condition;

import com.maxwellnie.velox.jpa.core.enums.QueryCondition;
import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;

public class ListConditionFragment extends ConditionFragment {
    private final QueryCondition in = QueryCondition.IN;
    private String column;
    private Object[] values;

    public ListConditionFragment(String column, Object[] values) {
        this.column = column;
        this.values = values;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    @Override
    public String getNativeSql() {
        if (!StringUtils.isNullOrEmpty(column) && values != null && values.length != 0)
            return column + " " + in + " " + StringUtils.buildValuesSql(values.length).substring(6, values.length);
        else
            return "";
    }
}
