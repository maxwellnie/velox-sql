package com.crazy.sql.core.jdbc.sql.condition;

import com.crazy.sql.core.enums.QueryCondition;
import com.crazy.sql.core.utils.java.StringUtils;

/**
 * 查询条件关键字
 */
public class NormalConditionFragment extends ConditionFragment {
    private String column;
    private QueryCondition condition;
    private Object value;

    public NormalConditionFragment(String column, QueryCondition condition, Object value) {
        this.column = column;
        this.condition = condition;
        this.value = value;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public QueryCondition getCondition() {
        return condition;
    }

    public void setCondition(QueryCondition condition) {
        this.condition = condition;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "NormalConditionFragment{" +
                "field='" + column + '\'' +
                ", condition=" + condition +
                ", value=" + value +
                '}';
    }

    @Override
    public String getNativeSql() {
        if(!StringUtils.isNullOrEmpty(column)&&condition!=null)
            return column +" "+condition+" ?";
        else
            return "";
    }
}
