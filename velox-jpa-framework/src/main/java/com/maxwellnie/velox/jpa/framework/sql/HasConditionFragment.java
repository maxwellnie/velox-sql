package com.maxwellnie.velox.jpa.framework.sql;

import com.maxwellnie.velox.jpa.core.jdbc.sql.SqlFragment;
import com.maxwellnie.velox.jpa.framework.sql.condition.ConditionFragment;

import java.util.LinkedList;

/**
 * @author Maxwell Nie
 */
public abstract class HasConditionFragment implements SqlFragment {
    private LinkedList<ConditionFragment> conditionFragments = new LinkedList<>();

    public LinkedList<ConditionFragment> getConditionFragments() {
        return conditionFragments;
    }

    public void setConditionFragments(LinkedList<ConditionFragment> conditionFragments) {
        this.conditionFragments = conditionFragments;
    }

    public void addConditionFragment(ConditionFragment conditionFragment) {
        this.conditionFragments.add(conditionFragment);
    }
}
