package com.maxwellnie.vleox.jpa.core.utils.java;

import com.maxwellnie.vleox.jpa.core.jdbc.sql.SqlFragment;

/**
 * @author Maxwell Nie
 */
public class ObjectUtils {
    /**
     * 判断是否为一个空的sql片段
     *
     * @param sqlFragment
     * @return
     */
    public static boolean notEmptyFragment(SqlFragment sqlFragment) {
        if (sqlFragment == null)
            return false;
        else if (StringUtils.isNullOrEmpty(sqlFragment.getNativeSql()))
            return false;
        else
            return true;
    }
}
