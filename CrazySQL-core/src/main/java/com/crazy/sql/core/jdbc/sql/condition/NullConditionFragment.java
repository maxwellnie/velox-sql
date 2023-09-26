package com.crazy.sql.core.jdbc.sql.condition;

import com.crazy.sql.core.enums.QueryCondition;

public class NullConditionFragment extends ConditionFragment {
    private String column;
    private QueryCondition condition;

    public NullConditionFragment(String column, QueryCondition condition) {
        this.column = column;
        this.condition = condition;
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
    @Override
    public String getNativeSql() {
        return column +" "+condition;
    }
}
