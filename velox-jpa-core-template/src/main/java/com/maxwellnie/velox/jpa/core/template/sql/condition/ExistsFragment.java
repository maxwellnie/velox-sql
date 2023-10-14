package com.maxwellnie.velox.jpa.core.template.sql.condition;

import com.maxwellnie.velox.jpa.core.enums.QueryCondition;
import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;

/**
 * @author Maxwell Nie
 */
public class ExistsFragment extends ConditionFragment {
    private String column;
    public ExistsFragment() {
    }

    public ExistsFragment(String column) {
        this.column = column;
    }

    @Override
    public String getNativeSql() {
        if(!StringUtils.isNullOrEmpty(column))
            return column+QueryCondition.EXISTS;
        return null;
    }
}
