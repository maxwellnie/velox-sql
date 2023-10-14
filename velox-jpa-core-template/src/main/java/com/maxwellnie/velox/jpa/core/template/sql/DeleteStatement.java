package com.maxwellnie.velox.jpa.core.template.sql;

import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;

/**
 * @author Maxwell Nie
 */
public class DeleteStatement extends SqlStatement {

    @Override
    public void integratingResource() {
        StringBuilder sql = new StringBuilder();
        if (!StringUtils.isNullOrEmpty(getTableName())) {
            sql.append("DELETE FROM ")
                    .append(getTableName())
                    .append(" ");
            SqlFragmentUtils.buildWhereAndLast(this, sql);
        }
        setNativeSql(sql.append(";").toString());
    }
}
