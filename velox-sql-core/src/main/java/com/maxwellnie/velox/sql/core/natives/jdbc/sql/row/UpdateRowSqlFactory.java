package com.maxwellnie.velox.sql.core.natives.jdbc.sql.row;

import com.maxwellnie.velox.sql.core.meta.MetaData;
import com.maxwellnie.velox.sql.core.natives.dao.SqlDecorator;
import com.maxwellnie.velox.sql.core.natives.exception.ExecutorException;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlPool;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlType;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.creator.SqlCreator;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfo;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.sql.core.natives.type.convertor.ConvertorManager;
import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class UpdateRowSqlFactory implements RowSqlFactory {
    @Override
    public RowSql getRowSql(MetaData metaData) throws ExecutorException {
        assert metaData != null : "Meta data is null.";
        TableInfo tableInfo = metaData.getProperty("tableInfo");
        assert tableInfo != null : "TableInfo is null.";
        Collection<?> entityObjects = metaData.getProperty("entityObjects");
        assert entityObjects != null : "The entityObject is null.";
        SqlDecorator<?> sqlDecorator = metaData.getProperty("sqlDecorator");
        SqlType sqlType = metaData.getProperty("sqlType");
        assert sqlType != null : "The sqlType is null.";
        String tableName = tableInfo.getTableName();
        List<List<Object>> params = new LinkedList<>();
        StringBuilder setSql = new StringBuilder();
        List<TypeConvertor<?>> typeConvertors = new LinkedList<>();
        boolean justSetParam = false;
        StringBuilder otherSql = new StringBuilder();
        for (Object entityObject : entityObjects) {
            List<Object> param = new LinkedList<>();
            for (ColumnInfo columnInfo : tableInfo.getColumnMappedMap().values()) {
                try {
                    param.add(columnInfo.getColumnMappedField().get(entityObject));
                    if (!justSetParam) {
                        setSql.append(tableName).append(".").append(columnInfo.getColumnName()).append("=?,");
                        typeConvertors.add(columnInfo.getTypeConvertor());
                    }

                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new ExecutorException(e);
                }
            }
            if (sqlDecorator != null) {
                if (sqlDecorator.getWhereFragment() != null) {
                    StringBuilder inlineSqlFragment = new StringBuilder("");
                    for (int i = 0; i < sqlDecorator.getWhereFragment().getInlineSql().size(); i++) {
                        param.addAll(sqlDecorator.getWhereFragment().getInlineSql().get(i).getParams());
                        if (!justSetParam) {
                            for (Object obj : sqlDecorator.getWhereFragment().getInlineSql().get(i).getParams())
                                typeConvertors.add(ConvertorManager.getConvertor(obj.getClass()));
                            inlineSqlFragment.append(sqlDecorator.getWhereFragment().getInlineSql().get(i).getSql());
                            if (i < sqlDecorator.getWhereFragment().getInlineSql().size() - 1) {
                                inlineSqlFragment.append(SqlPool.SPACE);
                            }
                        }
                    }
                    otherSql.append(SqlPool.SPACE).append(SqlCreator.create(sqlDecorator.getWhereFragment().getSql(), inlineSqlFragment.toString()));
                }
                if (sqlDecorator.getApplySql() != null) {
                    otherSql.append(SqlPool.SPACE).append(sqlDecorator.getApplySql().getSql());
                    for (Object obj : sqlDecorator.getApplySql().getParams()) {
                        typeConvertors.add(ConvertorManager.getConvertor(obj.getClass()));
                    }
                    param.addAll(sqlDecorator.getApplySql().getParams());
                }
            }
            justSetParam = true;
            params.add(param);
        }

        RowSql rowSql = new RowSql();
        rowSql.setNativeSql(SqlCreator.create(SqlPool.UPDATE, tableName, setSql.substring(0, setSql.length() - 1), otherSql.toString()));
        rowSql.setParams(params);
        rowSql.setSqlType(sqlType);
        rowSql.setTypeConvertors(typeConvertors);
        rowSql.setSqlDecorator(sqlDecorator);
        return rowSql;
    }
}
