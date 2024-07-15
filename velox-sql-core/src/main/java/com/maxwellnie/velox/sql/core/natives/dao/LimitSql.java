package com.maxwellnie.velox.sql.core.natives.dao;

import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlPool;

/**
 * @author Maxwell Nie
 */
public class LimitSql extends BaseSql {
    protected String sql = SqlPool.SPACE;
    protected boolean isInject = true;
    protected long start = 0;
    protected long offset = 0;
    protected boolean lock;

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public void setSql(String sql) {
        this.sql = sql;
    }


    @Override
    public boolean isInject() {
        return isInject;
    }

    @Override
    public void setInject(boolean inject) {
        isInject = inject;
    }

    public boolean getLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }
}
