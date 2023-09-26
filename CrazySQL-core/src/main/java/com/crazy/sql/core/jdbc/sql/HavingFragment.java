package com.crazy.sql.core.jdbc.sql;

import com.crazy.sql.core.utils.java.StringUtils;

/**
 * @author Akiba no ichiichiyoha
 */
public class HavingFragment extends HasConditionFragment {

    @Override
    public String getNativeSql() {
        String sql="";
        if (getConditionFragments() !=null&&!getConditionFragments().isEmpty()){
            String str=SqlFragmentUtils.buildCondition(getConditionFragments());
            if(!StringUtils.isNullOrEmpty(str))
                sql= "HAVING "+ str;
        }
        return sql;
    }
}
