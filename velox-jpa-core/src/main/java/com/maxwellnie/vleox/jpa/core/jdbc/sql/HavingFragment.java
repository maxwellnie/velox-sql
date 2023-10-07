package com.maxwellnie.vleox.jpa.core.jdbc.sql;

import com.maxwellnie.vleox.jpa.core.utils.java.StringUtils;

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
