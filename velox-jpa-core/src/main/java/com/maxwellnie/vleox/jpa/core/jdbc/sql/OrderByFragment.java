package com.maxwellnie.vleox.jpa.core.jdbc.sql;

import com.maxwellnie.vleox.jpa.core.utils.java.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class OrderByFragment implements SqlFragment {
    private List<OrderByColumn> columns = new LinkedList<>();

    public List<OrderByColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<OrderByColumn> columns) {
        this.columns = columns;
    }

    public void addColumn(OrderByColumn orderByColumn) {
        this.columns.add(orderByColumn);
    }

    @Override
    public String getNativeSql() {
        if (!columns.isEmpty()) {
            String obc = StringUtils.getAllNativeSql(columns);
            return "ORDER BY " + obc + " ";
        } else
            return "";
    }
}
