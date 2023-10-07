package com.maxwellnie.vleox.jpa.core.jdbc.sql;

import com.maxwellnie.vleox.jpa.core.utils.java.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class GroupByFragment implements SqlFragment {
    private List<String> columns = new LinkedList<>();

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    @Override
    public String getNativeSql() {
        if (!columns.isEmpty()) {
            String nativeFragment = StringUtils.getTupleStr(columns);
            return "GROUP BY " + nativeFragment.substring(1, nativeFragment.length() - 1) + " ";
        } else
            return "";
    }
}
