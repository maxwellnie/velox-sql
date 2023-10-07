package com.maxwellnie.vleox.jpa.core.jdbc.sql;

import com.maxwellnie.vleox.jpa.core.utils.java.ObjectUtils;
import com.maxwellnie.vleox.jpa.core.utils.java.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 查询型sql语句实体
 *
 * @author Maxwell Nie
 */
public class SelectStatement extends SqlStatement {
    protected List<String> selectedColumns = new LinkedList<>();
    protected JoinOnFragment joinOnStatement;
    protected GroupByFragment groupByStatement;
    protected HavingFragment havingStatement;
    protected OrderByFragment orderByFragment;
    protected LimitFragment limitFragment;

    public List<String> getSelectedColumns() {
        return selectedColumns;
    }

    public void setSelectedColumns(List<String> selectedColumns) {
        this.selectedColumns = selectedColumns;
    }

    public JoinOnFragment getJoinOnStatement() {
        return joinOnStatement;
    }

    public void setJoinOnStatement(JoinOnFragment joinOnStatement) {
        this.joinOnStatement = joinOnStatement;
    }

    public GroupByFragment getGroupByStatement() {
        return groupByStatement;
    }

    public void setGroupByStatement(GroupByFragment groupByStatement) {
        this.groupByStatement = groupByStatement;
    }

    public HavingFragment getHavingStatement() {
        return havingStatement;
    }

    public void setHavingStatement(HavingFragment havingStatement) {
        this.havingStatement = havingStatement;
    }

    public OrderByFragment getOrderByFragment() {
        return orderByFragment;
    }

    public void setOrderByFragment(OrderByFragment orderByFragment) {
        this.orderByFragment = orderByFragment;
    }

    public LimitFragment getLimitFragment() {
        return limitFragment;
    }

    public void setLimitFragment(LimitFragment limitFragment) {
        this.limitFragment = limitFragment;
    }

    @Override
    public void integratingResource() {
        StringBuilder sql = new StringBuilder();
        if (!StringUtils.isNullOrEmpty(getTableName()) && !selectedColumns.isEmpty()) {
            String sc = StringUtils.getTupleStr(selectedColumns);
            sc = sc.substring(1, sc.length() - 1);
            sql.append("SELECT ")
                    .append(sc)
                    .append(" FROM ")
                    .append(getTableName())
                    .append(" ");
            handleSql(sql);
        }
        if (sql.length() != 0)
            setNativeSql(sql + ";");
    }

    protected void handleSql(StringBuilder sql) {
        if (ObjectUtils.notEmptyFragment(joinOnStatement)) {
            sql.append(joinOnStatement.getNativeSql());
            getValues().addAll(SqlFragmentUtils.getValues(joinOnStatement.getOnCondition()));
        }
        if (ObjectUtils.notEmptyFragment(getWhereFragment())) {
            sql.append(getWhereFragment().getNativeSql());
            getValues().addAll(SqlFragmentUtils.getValues(getWhereFragment().getConditionFragments()));
        }
        if (ObjectUtils.notEmptyFragment(groupByStatement)) {
            sql.append(groupByStatement.getNativeSql());
            if (ObjectUtils.notEmptyFragment(havingStatement)) {
                sql.append(havingStatement.getNativeSql());
                getValues().addAll(SqlFragmentUtils.getValues(havingStatement.getConditionFragments()));
            }
        }
        if (ObjectUtils.notEmptyFragment(orderByFragment)) {
            sql.append(orderByFragment.getNativeSql());
        }
        if (ObjectUtils.notEmptyFragment(limitFragment)) {
            sql.append(limitFragment.getNativeSql());
        }
        if (ObjectUtils.notEmptyFragment(getLastFragment())) {
            sql.append(getLastFragment().getNativeSql());
            getValues().addAll(Arrays.asList(getLastFragment().getValues()));
        }
    }
}
