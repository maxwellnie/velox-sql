package com.maxwellnie.velox.jpa.core.template.sql;

import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;

/**
 * @author Maxwell Nie
 */
public class SizeStatement extends SelectStatement {
    private String primaryName;

    public String getPrimaryName() {
        return primaryName;
    }

    public void setPrimaryName(String primaryName) {
        this.primaryName = primaryName;
    }

    @Override
    public void integratingResource() {
        boolean enableHandle = SqlFragmentUtils.notEmptyFragment(limitFragment);
        StringBuilder sql = new StringBuilder();
        StringBuilder selectSql = new StringBuilder();
        if (!StringUtils.isNullOrEmpty(getTableName()) && !selectedColumns.isEmpty()) {
            if (StringUtils.isNullOrEmpty(primaryName) || SqlFragmentUtils.notEmptyFragment(groupByStatement)) {
                if (enableHandle) {
                    sql.append("SELECT COUNT(*) FROM (");
                    selectSql.append("SELECT ")
                            .append(StringUtils.getTupleStr(selectedColumns))
                            .append(" FROM ")
                            .append(getTableName())
                            .append(" ");
                } else
                    sql.append("SELECT COUNT(*) FROM ")
                            .append(getTableName())
                            .append(" ");
            } else {
                if (enableHandle) {
                    sql.append("SELECT COUNT(")
                            .append(primaryName)
                            .append(")")
                            .append(" FROM (");
                    selectSql.append("SELECT ")
                            .append(primaryName)
                            .append(" FROM ")
                            .append(getTableName())
                            .append(" ");
                } else
                    sql.append("SELECT COUNT(")
                            .append(primaryName)
                            .append(") FROM ")
                            .append(getTableName())
                            .append(" ");
            }
            if (enableHandle) {
                handleSql(selectSql);
                sql.append(selectSql)
                        .append(") AS query_result");
            } else
                handleSql(sql);
        }
        if (sql.length() != 0)
            setNativeSql(sql.append(";").toString());
    }


}
