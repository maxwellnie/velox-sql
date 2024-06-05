package com.maxwellnie.velox.sql.core.natives.dao;

import java.util.List;

/**
 * @author Maxwell Nie
 */
public class VPage<T> implements Page<T>{
    private List<T> result;
    private long total;
    private long current;
    private long offset;

    public VPage(List<T> result, long total, long current, long offset) {
        this.result = result;
        this.total = total;
        this.current = current;
        this.offset = offset;
    }

    public VPage() {
    }

    @Override
    public List<T> getResult() {
        return result;
    }

    @Override
    public long getTotal() {
        return total;
    }

    @Override
    public void setTotal(long total) {
        this.total = total;
    }

    @Override
    public long getCurrent() {
        return current;
    }

    @Override
    public void setCurrent(long current) {
        this.current = current;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public void setOffset(long offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "VPage{" +
                "result=" + result +
                ", total=" + total +
                ", current=" + current +
                ", offset=" + offset +
                '}';
    }
}
