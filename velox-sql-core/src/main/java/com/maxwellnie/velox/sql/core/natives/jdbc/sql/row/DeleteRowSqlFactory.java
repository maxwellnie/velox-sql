package com.maxwellnie.velox.sql.core.natives.jdbc.sql.row;

import com.maxwellnie.velox.sql.core.meta.MetaData;
import com.maxwellnie.velox.sql.core.natives.dao.SqlDecorator;
import com.maxwellnie.velox.sql.core.natives.exception.ExecutorException;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlPool;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlType;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.creator.SqlCreator;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfo;
import com.maxwellnie.velox.sql.core.natives.type.convertor.ConvertorManager;
import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class DeleteRowSqlFactory implements RowSqlFactory {
    @Override
    public RowSql getRowSql(MetaData metaData) throws ExecutorException {
        assert metaData != null : "Meta data is null.";
        TableInfo tableInfo = metaData.getProperty("tableInfo");
        assert tableInfo != null : "TableInfo is null.";
        Serializable[] ids = metaData.getProperty("ids");
        SqlType sqlType = metaData.getProperty("sqlType");
        assert sqlType != null : "The sqlType is null.";
        String tableName = tableInfo.getTableName();
        List<List<Object>> params = new LinkedList<>();
        List<TypeConvertor<?>> typeConvertors = new LinkedList<>();
        StringBuilder otherSql = new StringBuilder();
        RowSql rowSql = new RowSql();
        rowSql.setParams(params);
        rowSql.setSqlType(sqlType);
        if (ids == null && !sqlType.equals(SqlType.BATCH_UPDATE)) {
            SqlDecorator<?> sqlDecorator = metaData.getProperty("sqlDecorator");
            List<Object> aRowSqlParams = new LinkedList<>();
            if (sqlDecorator != null) {
                if (sqlDecorator.getWhereFragment() != null) {
                    StringBuilder inlineSqlFragment = new StringBuilder("");
                    for (int i = 0; i < sqlDecorator.getWhereFragment().getInlineSql().size(); i++) {
                        inlineSqlFragment.append(sqlDecorator.getWhereFragment().getInlineSql().get(i).getSql());
                        aRowSqlParams.addAll(sqlDecorator.getWhereFragment().getInlineSql().get(i).getParams());
                        for (Object obj : sqlDecorator.getWhereFragment().getInlineSql().get(i).getParams())
                            typeConvertors.add(ConvertorManager.getConvertor(obj.getClass()));
                        if (i < sqlDecorator.getWhereFragment().getInlineSql().size() - 1) {
                            inlineSqlFragment.append(SqlPool.SPACE);
                        }
                    }
                    otherSql.append(SqlPool.SPACE).append(SqlCreator.create(sqlDecorator.getWhereFragment().getSql(), inlineSqlFragment.toString()));
                }
                if (sqlDecorator.getApplySql() != null) {
                    otherSql.append(SqlPool.SPACE).append(sqlDecorator.getApplySql().getSql());
                    for (Object obj : sqlDecorator.getApplySql().getParams()) {
                        typeConvertors.add(ConvertorManager.getConvertor(obj.getClass()));
                    }
                    aRowSqlParams.addAll(sqlDecorator.getApplySql().getParams());
                }
            }
            rowSql.setNativeSql(SqlCreator.create(SqlPool.DELETE, tableName, otherSql.toString()));
            params.add(aRowSqlParams);
            rowSql.setTypeConvertors(typeConvertors);
            rowSql.setSqlDecorator(sqlDecorator);
            return rowSql;
        } else {
            StringBuilder sql = new StringBuilder("");
            boolean justSetParam = false;
            for (Serializable id : ids) {
                otherSql = new StringBuilder("");
                List<Object> aRowSqlParams = new LinkedList<>();
                aRowSqlParams.add(id);
                params.add(aRowSqlParams);
                if (!justSetParam) {
                    rowSql.setTypeConvertors(Collections.singletonList(ConvertorManager.getConvertor(id.getClass())));
                    justSetParam = true;
                    otherSql.append(SqlCreator.create(SqlPool.WHERE, tableInfo.getPkColumn().getColumnName() + " = ?"));
                    sql.append(SqlCreator.create(SqlPool.DELETE, tableName, otherSql.toString()));
                }
            }
            rowSql.setNativeSql(sql.toString());
            return rowSql;
        }
    }
}
