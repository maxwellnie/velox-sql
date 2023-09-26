package com.crazy.sql.core.jdbc.sql;

/**
 * @author Akiba no ichiichiyoha
 */
public class LimitFragment implements SqlFragment {
    private long start;
    private long offset;

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    @Override
    public String getNativeSql() {
        if(start>=0&&offset>0)
            return "LIMIT "+start+","+offset+" ";
        else
            return "";
    }
}
