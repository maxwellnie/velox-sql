package com.maxwellnie.velox.jpa.framework.sql;

import com.maxwellnie.velox.jpa.core.enums.JoinType;
import com.maxwellnie.velox.jpa.core.jdbc.sql.SqlFragment;
import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;
import com.maxwellnie.velox.jpa.framework.sql.condition.ConditionFragment;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class JoinOnFragment implements SqlFragment {
    private String joinTable;
    private JoinType joinType = JoinType.LEFT;
    private List<ConditionFragment> onCondition = new LinkedList<>();

    public JoinOnFragment() {
    }

    public JoinOnFragment(String joinTable, JoinType joinType, List<ConditionFragment> onCondition) {
        this.joinTable = joinTable;
        this.joinType = joinType;
        this.onCondition = onCondition;
    }

    public String getJoinTable() {
        return joinTable;
    }

    public void setJoinTable(String joinTable) {
        this.joinTable = joinTable;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

    public List<ConditionFragment> getOnCondition() {
        return onCondition;
    }

    public void setOnCondition(List<ConditionFragment> onCondition) {
        this.onCondition = onCondition;
    }

    public void addOnCondition(ConditionFragment conditionFragment) {
        this.onCondition.add(conditionFragment);
    }

    @Override
    public String getNativeSql() {
        if (!StringUtils.isNullOrEmpty(joinTable) && joinType != null && !onCondition.isEmpty())
            return joinType.name() + " JOIN " + joinTable + " ON " + SqlFragmentUtils.buildCondition(onCondition) + " ";
        else
            return "";
    }
}
