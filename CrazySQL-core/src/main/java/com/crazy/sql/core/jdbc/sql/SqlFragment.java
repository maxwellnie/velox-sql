package com.crazy.sql.core.jdbc.sql;


/**
 * sql语句片段实体
 * @author Akiba no ichiichiyoha
 */
public interface SqlFragment {
    /**
     * 获取原生sql
     * @return
     */
    String getNativeSql();
}
