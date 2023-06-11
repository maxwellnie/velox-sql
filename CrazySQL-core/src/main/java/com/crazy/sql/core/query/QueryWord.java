package com.crazy.sql.core.query;

import com.crazy.sql.core.enums.QueryCondition;

/**
 * 查询条件关键字
 */
public class QueryWord {
    private String field;
    private QueryCondition condition;
    private Object value;

    public QueryWord(String field, QueryCondition condition, Object value) {
        this.field = field;
        this.condition = condition;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
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
        return "QueryWord{" +
                "field='" + field + '\'' +
                ", condition=" + condition +
                ", value=" + value +
                '}';
    }
}
