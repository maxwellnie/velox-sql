package com.crazy.sql.core.jdbc.sql.condition;

import com.crazy.sql.core.enums.QueryCondition;
import com.crazy.sql.core.utils.java.StringUtils;

public class BetweenAndConditionFragment extends ConditionFragment {
    private String column;
    private final QueryCondition between=QueryCondition.BETWEEN;
    private final QueryCondition and=QueryCondition.AND;
    private Object value1;
    private Object value2;

    public BetweenAndConditionFragment(String column, Object value1, Object value2) {
        this.column = column;
        this.value1 = value1;
        this.value2 = value2;
    }

    public Object getValue1() {
        return value1;
    }

    public void setValue1(Object value1) {
        this.value1 = value1;
    }

    public Object getValue2() {
        return value2;
    }

    public void setValue2(Object value2) {
        this.value2 = value2;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }


    @Override
    public String getNativeSql() {
        if(!StringUtils.isNullOrEmpty(column)&&value1!=null&&value2!=null)
            return column+" "+ between +" ? "+and+" ?";
        else
            return "";
    }
}
