package com.crazy.sql.core.jdbc.sql;

import com.crazy.sql.core.jdbc.sql.condition.ConditionFragment;

import java.util.LinkedList;

/**
 * @author Akiba no ichiichiyoha
 */
public abstract class HasConditionFragment implements SqlFragment{
    private LinkedList<ConditionFragment> conditionFragments = new LinkedList<>();

    public LinkedList<ConditionFragment> getConditionFragments() {
        return conditionFragments;
    }

    public void setConditionFragments(LinkedList<ConditionFragment> conditionFragments) {
        this.conditionFragments = conditionFragments;
    }
    public void addConditionFragment(ConditionFragment conditionFragment){
        this.conditionFragments.add(conditionFragment);
    }
}
