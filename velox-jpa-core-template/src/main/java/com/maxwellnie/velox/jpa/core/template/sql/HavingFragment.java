package com.maxwellnie.velox.jpa.core.template.sql;

import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;

/**
 * @author Maxwell Nie
 */
public class HavingFragment extends HasConditionFragment {

    @Override
    public String getNativeSql() {
        String sql = "";
        if (getConditionFragments() != null && !getConditionFragments().isEmpty()) {
            String str = SqlFragmentUtils.buildCondition(getConditionFragments());
            if (!StringUtils.isNullOrEmpty(str))
                sql = "HAVING " + str;
        }
        return sql;
    }
}
