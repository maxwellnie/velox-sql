package com.maxwellnie.vleox.jpa.core.jdbc.sql;

import com.maxwellnie.vleox.jpa.core.utils.java.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class UpdateStatement extends SqlStatement {
    private List<String> columns = new LinkedList<>();

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    @Override
    public void integratingResource() {
        StringBuilder sql = new StringBuilder();
        if (!StringUtils.isNullOrEmpty(getTableName()) && !columns.isEmpty()) {
            sql.append("UPDATE ")
                    .append(getTableName())
                    .append(" SET ")
                    .append(StringUtils.convertStr(columns, (ss) -> {
                        StringBuffer stringBuffer = (StringBuffer) ss[0];
                        String s = (String) ss[1];
                        if (!StringUtils.isNullOrEmpty(s))
                            stringBuffer.append(s).append("=?,");
                        return null;
                    }, (stringBuffer -> stringBuffer.length() <= 0 ? "" : stringBuffer.substring(0, stringBuffer.length() - 1))))
                    .append(" ");
            SqlFragmentUtils.buildWhereAndLast(this, sql);
        }
        if (sql.length() != 0)
            setNativeSql(sql.append(";").toString());
    }
}
