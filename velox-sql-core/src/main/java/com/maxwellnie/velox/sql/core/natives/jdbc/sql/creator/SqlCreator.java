package com.maxwellnie.velox.sql.core.natives.jdbc.sql.creator;

import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlPool;

import java.util.concurrent.ConcurrentHashMap;

/**
 * SQL构造器
 *
 * @author Maxwell Nie
 */
public class SqlCreator {
    private static final ConcurrentHashMap<String, String[]> SQL_INJECT_MAP = new ConcurrentHashMap<>();

    /**
     * 向含有占位符的SQL注入SQL片段以构造完整的SQL
     *
     * @param constSql     含有占位符的SQL
     * @param sqlFragments 注入SQL片段
     * @return 构造好的SQL
     */
    public static String create(String constSql, String... sqlFragments) {
        /**
         * 注入SQL片段为空
         */
        if (sqlFragments == null || sqlFragments.length == 0)
            return null;
        else {

            /**
             * 按规则切分SQL片段，不会出现数据不一致的情况，因为key和value都是由用户输入的含有占位符的SQL所决定，且结果是一致的。
             */
            String[] sqlArrayFragment = getSqlArrayFragment(constSql);
            /**
             * [-ele1-|-ele2-|-ele3-]
             * injectPlaceHolderLength = elementsLength
             */
            if (sqlArrayFragment.length - 1 != sqlFragments.length)
                throw new IllegalArgumentException("sqlFragments array length is not equal to sqlFragments length");
            /**
             * 注入SQL片段，将一个SQL片段数组拼接成一个SQL语句。
             */
            StringBuilder rowSql = new StringBuilder(sqlArrayFragment[0]);
            for (int i = 0; i < sqlFragments.length; i++) {
                if (sqlFragments[i] != null) {
                    rowSql.append(sqlFragments[i])
                            .append(sqlArrayFragment[i + 1]);
                }
            }
            return rowSql.toString();
        }
    }

    /**
     * 按规则切分SQL片段，不会出现数据不一致的情况，因为key和value都是由用户输入的含有占位符的SQL所决定，且结果满足唯一映射。
     *
     * @param constSql 含有占位符的SQL
     * @return 切分后的SQL片段数组
     */
    private static String[] getSqlArrayFragment(String constSql) {
        String[] sqlArrayFragment;
        if (SQL_INJECT_MAP.containsKey(constSql))
            sqlArrayFragment = SQL_INJECT_MAP.get(constSql);
        else {
            sqlArrayFragment = constSql.split(SqlPool.SQL_INJECT_PLACE_HOLDER, -2);
            SQL_INJECT_MAP.put(constSql, sqlArrayFragment);
        }
        return sqlArrayFragment;
    }
}
