package com.maxwellnie.velox.sql.core.natives.jdbc.sql.row;

import com.maxwellnie.velox.sql.core.natives.dao.SqlDecorator;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlType;
import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Maxwell Nie
 */
public class RowSql{
    private String nativeSql;
    private List<List<Object>> params = Collections.synchronizedList(new LinkedList<>());
    private List<TypeConvertor<?>> typeConvertors = Collections.synchronizedList(new LinkedList<>());
    private SqlType sqlType;
    private SqlDecorator<?> sqlDecorator;

    public String getNativeSql() {
        return nativeSql;
    }

    public void setNativeSql(String nativeSql) {
        this.nativeSql = nativeSql;
    }

    public List<List<Object>> getParams() {
        return params;
    }

    public void setParams(List<List<Object>> params) {
        this.params = params;
    }

    public List<TypeConvertor<?>> getTypeConvertors() {
        return typeConvertors;
    }

    public void setTypeConvertors(List<TypeConvertor<?>> typeConvertors) {
        this.typeConvertors = typeConvertors;
    }

    public SqlType getSqlType() {
        return sqlType;
    }

    public void setSqlType(SqlType sqlType) {
        this.sqlType = sqlType;
    }

    public SqlDecorator<?> getSqlDecorator() {
        return sqlDecorator;
    }

    public void setSqlDecorator(SqlDecorator<?> sqlDecorator) {
        this.sqlDecorator = sqlDecorator;
    }

    @Override
    public String toString() {
        return nativeSql;
    }
}
