package com.maxwellnie.vleox.jpa.core.jdbc.sql.condition;

import com.maxwellnie.vleox.jpa.core.utils.java.StringUtils;

/**
 * @author Maxwell Nie
 */
public class LikeFragment extends ConditionFragment {
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int ALL = 2;
    private String column;
    private Object value;

    public LikeFragment(String column, Object value, int mode) {
        this.column = column;
        String v = "%" + value.toString() + "%";
        if (mode == 0)
            v = "%" + value.toString();
        else if (mode == 1)
            v = value.toString() + "%";
        this.value = v;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        String v = "%" + value.toString() + "%";
        this.value = v;
    }

    public void setValue(Object value, int mode) {
        String v = "%" + value.toString() + "%";
        if (mode == 0)
            v = "%" + value.toString();
        else if (mode == 1)
            v = value.toString() + "%";
        this.value = v;
    }

    @Override
    public String getNativeSql() {
        if (!StringUtils.isNullOrEmpty(column) && value != null)
            return " " + column + " LIKE ?";
        return "";
    }
}
