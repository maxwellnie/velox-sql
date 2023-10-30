package com.maxwellnie.velox.jpa.framework.sql;

import com.maxwellnie.velox.jpa.core.jdbc.sql.SqlFragment;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 更易于使用的SqlFragment
 * @author Maxwell Nie
 */
public class SimpleSqlFragment implements SqlFragment {
    private List<Object> params = new LinkedList<>();
    private StringBuffer nativeSql;

    @Override
    public String getNativeSql() {
        return this.nativeSql.toString();
    }

    public void setNativeSql(String nativeSql) {
        this.nativeSql = new StringBuffer(nativeSql);
    }
    public void addSql(String sql,Object... params){
        this.nativeSql.append(" ").append(sql);
        this.params.addAll(Arrays.asList(params));
    }
    public List<Object> getParams() {
        return params;
    }
    public void addParam(Object param){
        this.params.add(param);
    }
    public void setParam(List<Object> params) {
        this.params = params;
    }
}
