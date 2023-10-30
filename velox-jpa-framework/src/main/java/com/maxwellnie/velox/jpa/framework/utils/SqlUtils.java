package com.maxwellnie.velox.jpa.framework.utils;

import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;
import com.maxwellnie.velox.jpa.framework.sql.SqlBuilder;
import com.maxwellnie.velox.jpa.framework.sql.SqlFragmentUtils;
import com.maxwellnie.velox.jpa.framework.sql.condition.ConditionFragment;

import java.util.Arrays;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class SqlUtils {
    public static String buildSql(SqlBuilder<?> sqlBuilder, List<Object> params){
        if(sqlBuilder == null)
            return "";
        else {
            StringBuffer stringBuffer=new StringBuffer(" ");

            if(sqlBuilder.getWhereFragment()!=null&&!StringUtils.isNullOrEmpty(sqlBuilder.getWhereFragment().getNativeSql())){
                stringBuffer.append(sqlBuilder.getWhereFragment().getNativeSql());
                params.addAll(SqlFragmentUtils.getValues(sqlBuilder.getWhereFragment().getConditionFragments()));
            }

            if(sqlBuilder.getGroupByFragment()!=null&&!StringUtils.isNullOrEmpty(sqlBuilder.getGroupByFragment().getNativeSql())){
                stringBuffer.append(" ").append(sqlBuilder.getGroupByFragment().getNativeSql());
                if(sqlBuilder.getHavingFragment()!=null&&!StringUtils.isNullOrEmpty(sqlBuilder.getHavingFragment().getNativeSql())) {
                    stringBuffer.append(" ").append(sqlBuilder.getHavingFragment().getNativeSql());
                    params.addAll(SqlFragmentUtils.getValues(sqlBuilder.getHavingFragment().getConditionFragments()));
                }
            }

            if(sqlBuilder.getOrderByFragment()!=null&&!StringUtils.isNullOrEmpty(sqlBuilder.getOrderByFragment().getNativeSql()))
                stringBuffer.append(" ").append(sqlBuilder.getOrderByFragment().getNativeSql());

            if(sqlBuilder.getLimitFragment()!=null&&!StringUtils.isNullOrEmpty(sqlBuilder.getLimitFragment().getNativeSql())){
                stringBuffer.append(" ").append(sqlBuilder.getLimitFragment().getNativeSql());
            }

            if(sqlBuilder.getLastFragment()!=null&&!StringUtils.isNullOrEmpty(sqlBuilder.getLastFragment().getNativeSql())){
                stringBuffer.append(" ").append(sqlBuilder.getLastFragment().getNativeSql());
                params.addAll(Arrays.asList(sqlBuilder.getLastFragment().getValues()));
            }
            return stringBuffer.append(";").toString();
        }
    }
}
