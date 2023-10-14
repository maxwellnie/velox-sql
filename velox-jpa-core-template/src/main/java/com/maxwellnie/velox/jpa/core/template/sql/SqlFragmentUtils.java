package com.maxwellnie.velox.jpa.core.template.sql;

import com.maxwellnie.velox.jpa.core.exception.TypeNotEqualsException;
import com.maxwellnie.velox.jpa.core.jdbc.sql.SqlFragment;
import com.maxwellnie.velox.jpa.core.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.template.dao.SqlBuilder;
import com.maxwellnie.velox.jpa.core.template.sql.condition.*;
import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SqlFragmentUtils {
    private static final Logger logger= LoggerFactory.getLogger(SqlFragmentUtils.class);
    public static InsertStatement getInsertStatement(Object obj, TableInfo tableInfo, SqlBuilder<?> sqlBuilder) {
        if (!obj.getClass().equals(tableInfo.getMappedClazz()))
            throw new TypeNotEqualsException("被处理实体的类型与提供实体信息的类型不同");
        else {
            InsertStatement insertStatement = new InsertStatement();
            insertStatement.setTableName(tableInfo.getTableName());
            insertStatement.setColumns(tableInfo.getColumnMappedMap().values().stream().map(ColumnInfo::getColumnName).collect(Collectors.toList()));
            insertStatement.setValues(tableInfo.getColumnMappedMap().values().stream().map(columnInfo -> {
                try {
                    return columnInfo.getColumnMappedField().get(obj);
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage()+"\t\n"+e.getCause());
                    return null;
                }
            }).collect(Collectors.toList()));
            insertStatement.setLastFragment(sqlBuilder.getLastFragment());
            return insertStatement;
        }
    }

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

    public static void buildWhereAndLast(SqlStatement statement, StringBuilder sql) {
        if (notEmptyFragment(statement.getWhereFragment())) {
            sql.append(statement.getWhereFragment().getNativeSql());
            statement.getValues().addAll(SqlFragmentUtils.getValues(statement.getWhereFragment().getConditionFragments()));
        }
        if (notEmptyFragment(statement.getLastFragment())) {
            sql.append(statement.getLastFragment().getNativeSql());
            statement.getValues().addAll(Arrays.asList(statement.getLastFragment().getValues()));
        }
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
