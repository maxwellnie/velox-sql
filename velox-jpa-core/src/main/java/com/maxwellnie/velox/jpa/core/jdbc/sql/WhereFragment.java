package com.maxwellnie.velox.jpa.core.jdbc.sql;

import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;

/**
 * @author Maxwell Nie
 */
public class WhereFragment extends HasConditionFragment {

    @Override
    public String getNativeSql() {
        String sql = "";
        if (getConditionFragments() != null && !getConditionFragments().isEmpty()) {
            String str = SqlFragmentUtils.buildCondition(getConditionFragments());
            if (!StringUtils.isNullOrEmpty(str))
                sql = "WHERE " + str;
        }
        return sql;
    }
}
