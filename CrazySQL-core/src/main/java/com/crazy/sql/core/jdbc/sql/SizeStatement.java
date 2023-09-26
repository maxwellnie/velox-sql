package com.crazy.sql.core.jdbc.sql;

import com.crazy.sql.core.utils.java.ObjectUtils;
import com.crazy.sql.core.utils.java.StringUtils;

/**
 * @author Akiba no ichiichiyoha
 */
public class SizeStatement extends SelectStatement{
    private String primaryName;

    public String getPrimaryName() {
        return primaryName;
    }

    public void setPrimaryName(String primaryName) {
        this.primaryName = primaryName;
    }

    @Override
    public void integratingResource() {
        boolean enableHandle=ObjectUtils.notEmptyFragment(limitFragment);
        StringBuilder sql=new StringBuilder();
        StringBuilder selectSql=new StringBuilder();
        if(!StringUtils.isNullOrEmpty(getTableName()) &&!selectedColumns.isEmpty()){
            if(StringUtils.isNullOrEmpty(primaryName)||ObjectUtils.notEmptyFragment(groupByStatement)) {
                if (enableHandle) {
                    sql.append("SELECT COUNT(*) FROM (");
                    selectSql.append("SELECT ")
                            .append(StringUtils.getTupleStr(selectedColumns))
                            .append(" FROM ")
                            .append(getTableName())
                            .append(" ");
                }
                 else
                    sql.append("SELECT COUNT(*) FROM ")
                            .append(getTableName())
                            .append(" ");
            }else {
                if (enableHandle) {
                    sql.append("SELECT COUNT(")
                            .append(primaryName)
                            .append(")")
                            .append(" FROM (");
                    selectSql.append("SELECT ")
                            .append(primaryName)
                            .append(" FROM ")
                            .append(getTableName())
                            .append(" ");
                }
                else
                    sql.append("SELECT COUNT(")
                            .append(primaryName)
                            .append(") FROM ")
                            .append(getTableName())
                            .append(" ");
            }
            if(enableHandle){
                handleSql(selectSql);
                sql.append(selectSql)
                        .append(") AS query_result");
            }
            else
                handleSql(sql);
        }
        if (sql.length()!=0)
            setNativeSql(sql.append(";").toString());
    }


}
