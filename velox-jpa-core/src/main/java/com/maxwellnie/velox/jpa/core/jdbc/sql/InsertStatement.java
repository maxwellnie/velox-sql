package com.maxwellnie.velox.jpa.core.jdbc.sql;

import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class InsertStatement extends SqlStatement {
    private List<String> columns = new LinkedList<>();

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    @Override
    public void integratingResource() {
        StringBuilder sql = new StringBuilder();
        if (!StringUtils.isNullOrEmpty(getTableName()) && !columns.isEmpty()) {
            sql.append("INSERT INTO ")
                    .append(getTableName())
                    .append(" ")
                    .append(StringUtils.getTupleStr(columns))
                    .append(" ")
                    .append(StringUtils.buildValuesSql(columns.size()));
            if (getLastFragment() != null && !StringUtils.isNullOrEmpty(getLastFragment().getNativeSql())) {
                sql.append(getLastFragment().getNativeSql());
                if (getLastFragment().getValues() != null && getLastFragment().getValues().length != 0)
                    getValues().addAll(Arrays.asList(getLastFragment().getValues()));
            }
        }
        if (sql.length() != 0)
            setNativeSql(sql + ";");
    }
}
