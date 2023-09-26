package com.crazy.sql.core.jdbc.sql;

import com.crazy.sql.core.utils.java.StringUtils;

/**
 * @author Akiba no ichiichiyoha
 */
public class DeleteStatement extends SqlStatement {

    @Override
    public void integratingResource() {
        StringBuilder sql=new StringBuilder();
        if(!StringUtils.isNullOrEmpty(getTableName())){
            sql.append("DELETE FROM ")
                    .append(getTableName())
                    .append(" ");
            SqlFragmentUtils.buildWhereAndLast(this,sql);
        }
        setNativeSql(sql.append(";").toString());
    }
}
