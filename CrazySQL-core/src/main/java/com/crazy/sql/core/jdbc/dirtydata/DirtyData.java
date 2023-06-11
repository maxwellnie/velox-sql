package com.crazy.sql.core.jdbc.dirtydata;

public class DirtyData {
    private int key;
    private int mode;
    private Object data;

    public DirtyData() {
    }

    public DirtyData( int key, Object data) {
        this.key = key;
        this.data = data;
    }

    public DirtyData( int key, int mode, Object data) {
        this.key = key;
        this.mode = mode;
        this.data = data;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
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
}
