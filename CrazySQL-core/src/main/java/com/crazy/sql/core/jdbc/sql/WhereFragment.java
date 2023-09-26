package com.crazy.sql.core.jdbc.sql;

import com.crazy.sql.core.utils.java.StringUtils;

/**
 * @author Akiba no ichiichiyoha
 */
public class WhereFragment extends HasConditionFragment {

    @Override
    public String getNativeSql() {
        String sql="";
        if (getConditionFragments() !=null&&!getConditionFragments().isEmpty()){
            String str=SqlFragmentUtils.buildCondition(getConditionFragments());
            if(!StringUtils.isNullOrEmpty(str))
                sql= "WHERE "+ str;
        }
        return sql;
    }
}
