package com.crazy.sql.core.jdbc.dirtydata;

import com.crazy.sql.core.utils.SQLUtils;

public class DirtyData {
    private String key;
    private int mode;
    private Object data;
    private long row;
    private SQLUtils sqlUtils;

    public DirtyData() {
    }

    public DirtyData( String key, Object data, long row,SQLUtils sqlUtils) {
        this.key = key;
        this.data = data;
        this.row = row;
        this.sqlUtils=sqlUtils;
    }

    public DirtyData(String key, int mode, Object data, long row,SQLUtils sqlUtils) {
        this.key = key;
        this.mode = mode;
        this.data = data;
        this.row = row;
        this.sqlUtils=sqlUtils;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public long getRow() {
        return row;
    }

    public void setRow(long row) {
        this.row = row;
    }

    public SQLUtils getSqlUtils() {
        return sqlUtils;
    }

    public void setSqlUtils(SQLUtils sqlUtils) {
        this.sqlUtils = sqlUtils;
    }
}
