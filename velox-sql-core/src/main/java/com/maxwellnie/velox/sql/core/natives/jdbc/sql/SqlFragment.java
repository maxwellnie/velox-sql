package com.maxwellnie.velox.sql.core.natives.jdbc.sql;


/**
 * sql语句片段实体
 *
 * @author Maxwell Nie
 */
public interface SqlFragment {
    /**
     * 获取原生sql
     *
     * @return
     */
    String getNativeSql();
}
