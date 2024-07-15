package com.maxwellnie.velox.sql.core.natives.dao;

import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlPool;

import java.util.List;

/**
 * @author Maxwell Nie
 */
public class OrderBySql extends BaseSql {
    protected String sql = SqlPool.ORDER_BY;
    protected boolean isInject = true;
    protected List<ParamInfo> paramInfos = new java.util.ArrayList<>();

    public void addParamInfo(ParamInfo paramInfo) {
        this.paramInfos.add(paramInfo);
    }

    public void addParamInfos(List<ParamInfo> paramInfos) {
        this.paramInfos.addAll(paramInfos);
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

    public List<ParamInfo> getParamInfos() {
        return paramInfos;
    }

    public void setParamInfos(List<ParamInfo> paramInfos) {
        this.paramInfos = paramInfos;
    }

    public static class ParamInfo {
        public String column;
        public String order;

        public ParamInfo(String column, String order) {
            this.column = column;
            this.order = order;
        }

        public String getColumn() {
            return column;
        }

        public String getOrder() {
            return order;
        }
    }
}
