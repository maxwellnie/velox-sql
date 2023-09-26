package com.crazy.sql.core.jdbc.sql;

import com.crazy.sql.core.utils.java.StringUtils;

/**
 * @author Akiba no ichiichiyoha
 */
public class OrderByColumn implements SqlFragment{
    private String column;
    private String mode;
    public static final String ASC="ASC";
    public static final String DESC="DESC";

    public OrderByColumn(String column, String mode) {
        this.column = column;
        this.mode = mode;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String getNativeSql() {
        if(!StringUtils.isNullOrEmpty(column)&&!StringUtils.isNullOrEmpty(mode))
            return column+" "+mode;
        else
            return "";
    }
}
