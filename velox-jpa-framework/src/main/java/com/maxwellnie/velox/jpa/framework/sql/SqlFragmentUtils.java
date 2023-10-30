package com.maxwellnie.velox.jpa.framework.sql;

import com.maxwellnie.velox.jpa.core.jdbc.sql.SqlFragment;
import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;
import com.maxwellnie.velox.jpa.framework.sql.condition.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SqlFragmentUtils {
    private static final Logger logger= LoggerFactory.getLogger(SqlFragmentUtils.class);

    public static List<Object> getValues(List<ConditionFragment> conditionFragments) {
        List<Object> list = new LinkedList<>();
        for (ConditionFragment conditionFragment : conditionFragments) {
            if (conditionFragment instanceof BetweenAndConditionFragment) {
                BetweenAndConditionFragment betweenAndConditionFragment = (BetweenAndConditionFragment) conditionFragment;
                if (notEmptyFragment(betweenAndConditionFragment)) {
                    list.add(betweenAndConditionFragment.getValue1());
                    list.add(betweenAndConditionFragment.getValue2());
                }
            } else if (conditionFragment instanceof ListConditionFragment) {
                ListConditionFragment listConditionFragment = (ListConditionFragment) conditionFragment;
                if (notEmptyFragment(listConditionFragment)) {
                    if (listConditionFragment.getValues() != null)
                        list.addAll(Arrays.asList(listConditionFragment.getValues()));
                }
            } else if (conditionFragment instanceof LikeFragment) {
                LikeFragment likeFragment = (LikeFragment) conditionFragment;
                if (notEmptyFragment(likeFragment)) {
                    if (likeFragment.getValue() != null)
                        list.add(likeFragment.getValue());
                }
            } else if (conditionFragment instanceof NormalConditionFragment) {
                NormalConditionFragment normalConditionFragment = (NormalConditionFragment) conditionFragment;
                if (notEmptyFragment(normalConditionFragment)) {
                    if (normalConditionFragment.getValue() != null)
                        list.add(normalConditionFragment.getValue());
                }
            }
        }
        return list;
    }

    public static String buildCondition(List<ConditionFragment> conditions) {
        StringBuffer stringBuffer = new StringBuffer();
        int length = 0;
        for (ConditionFragment condition : conditions) {
            if (notEmptyFragment(condition) && condition.getRelationShip() != null) {
                stringBuffer.append(" ").append(condition.getNativeSql()).append(" ").append(condition.getRelationShip().name());
                length = condition.getRelationShip().name().length();
            }
        }
        return stringBuffer.substring(0, stringBuffer.length() - length);
    }

    /**
     * 判断是否为一个空的sql片段
     *
     * @param sqlFragment
     * @return
     */
    public static boolean notEmptyFragment(SqlFragment sqlFragment) {
        if (sqlFragment == null)
            return false;
        else return !StringUtils.isNullOrEmpty(sqlFragment.getNativeSql());
    }
}
