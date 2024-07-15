package com.maxwellnie.velox.sql.core.natives.jdbc.sql.row;

import com.maxwellnie.velox.sql.core.config.simple.SingletonConfiguration;
import com.maxwellnie.velox.sql.core.meta.MetaData;
import com.maxwellnie.velox.sql.core.natives.dao.SqlDecorator;
import com.maxwellnie.velox.sql.core.natives.exception.ExecutorException;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlPool;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlType;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.creator.SqlCreator;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfo;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfoManager;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.join.JoinInfo;
import com.maxwellnie.velox.sql.core.natives.type.convertor.ConvertorManager;
import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;
import com.maxwellnie.velox.sql.core.utils.java.StringUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class QueryRowSqlFactory implements RowSqlFactory {
    @Override
    public RowSql getRowSql(MetaData metaData) throws ExecutorException {
        assert metaData != null : "Meta data is null.";
        TableInfo tableInfo = metaData.getProperty("tableInfo");
        assert tableInfo != null : "TableInfo is null.";
        SqlType sqlType = metaData.getProperty("sqlType");
        assert sqlType != null : "The sqlType is null.";
        SqlDecorator<?> sqlDecorator = metaData.getProperty("sqlDecorator");
        String tableName = tableInfo.getTableName();
        List<List<Object>> params = new LinkedList<>();
        StringBuilder columnSql = new StringBuilder();
        List<TypeConvertor<?>> typeConvertors = new LinkedList<>();
        handleColumns(tableInfo, tableName, columnSql, typeConvertors, null);
        StringBuilder fromSql = new StringBuilder(tableName);
        if (!tableInfo.getJoinInfos().isEmpty()) {
            StringBuilder joinedSql = new StringBuilder(SqlPool.SPACE);
            handleJoinInfos(tableInfo, tableName, columnSql, typeConvertors, joinedSql);
            fromSql.append(joinedSql);
        }
        List<Object> aRowSqlParams = new LinkedList<>();
        StringBuilder otherSql = new StringBuilder();
        handleSqlDecoratorObj(sqlDecorator, aRowSqlParams, otherSql, typeConvertors);
        params.add(aRowSqlParams);
        RowSql rowSql = instantiationRowSql(sqlType, params, columnSql, typeConvertors, fromSql, otherSql.toString());
        if (sqlDecorator != null && sqlDecorator.getLimitFragment() != null) {
            otherSql.append(SqlPool.SPACE).append(sqlDecorator.getLimitFragment().getSql());
            rowSql = SingletonConfiguration.getInstance().getDialect().getDialectRowSql(rowSql, sqlDecorator.getLimitFragment().getStart(), sqlDecorator.getLimitFragment().getOffset());
        }
        rowSql.setSqlDecorator(sqlDecorator);
        return rowSql;
    }

    /**
     * 处理sql装饰器
     *
     * @param sqlDecorator
     * @param aRowSqlParams
     * @param otherSql
     * @param typeConvertors
     */
    private void handleSqlDecoratorObj(SqlDecorator<?> sqlDecorator, List<Object> aRowSqlParams, StringBuilder otherSql, List<TypeConvertor<?>> typeConvertors) {
        if (sqlDecorator != null) {
            if (sqlDecorator.getWhereFragment() != null) {
                StringBuilder inlineSqlFragment = new StringBuilder("");
                for (int i = 0; i < sqlDecorator.getWhereFragment().getInlineSql().size(); i++) {
                    inlineSqlFragment.append(sqlDecorator.getWhereFragment().getInlineSql().get(i).getSql());
                    for (Object obj : sqlDecorator.getWhereFragment().getInlineSql().get(i).getParams())
                        typeConvertors.add(ConvertorManager.getConvertor(obj.getClass()));
                    aRowSqlParams.addAll(sqlDecorator.getWhereFragment().getInlineSql().get(i).getParams());
                    if (i < sqlDecorator.getWhereFragment().getInlineSql().size() - 1) {
                        inlineSqlFragment.append(SqlPool.SPACE);
                    }
                }
                otherSql.append(SqlPool.SPACE).append(SqlCreator.create(sqlDecorator.getWhereFragment().getSql(), inlineSqlFragment.toString()));
            }
            if (sqlDecorator.getGroupByFragment() != null) {
                otherSql.append(SqlPool.SPACE).append(sqlDecorator.getGroupByFragment().getSql());
            }
            if (sqlDecorator.getHavingFragment() != null) {
                StringBuilder inlineSqlFragment = new StringBuilder("");
                for (int i = 0; i < sqlDecorator.getHavingFragment().getInlineSql().size(); i++) {
                    inlineSqlFragment.append(sqlDecorator.getHavingFragment().getInlineSql().get(i).getSql());
                    aRowSqlParams.addAll(sqlDecorator.getHavingFragment().getInlineSql().get(i).getParams());
                    for (Object obj : sqlDecorator.getHavingFragment().getInlineSql().get(i).getParams())
                        typeConvertors.add(ConvertorManager.getConvertor(obj.getClass()));
                    if (i < sqlDecorator.getHavingFragment().getInlineSql().size() - 1) {
                        inlineSqlFragment.append(SqlPool.SPACE);
                    }
                }
                otherSql.append(SqlPool.SPACE).append(SqlCreator.create(sqlDecorator.getHavingFragment().getSql(), inlineSqlFragment.toString()));
            }
            if (sqlDecorator.getOrderByFragment() != null) {
                otherSql.append(SqlPool.SPACE).append(sqlDecorator.getOrderByFragment().getSql());
            }
            if (sqlDecorator.getApplySql() != null) {
                otherSql.append(SqlPool.SPACE).append(sqlDecorator.getApplySql().getSql());
                for (Object obj : sqlDecorator.getApplySql().getParams()) {
                    typeConvertors.add(ConvertorManager.getConvertor(obj.getClass()));
                }
                aRowSqlParams.addAll(sqlDecorator.getApplySql().getParams());
            }
        }
    }

    /**
     * 处理join信息
     *
     * @param tableInfo
     * @param tableName
     * @param columnSql
     * @param typeConvertors
     * @param joinedSql
     */
    private void handleJoinInfos(TableInfo tableInfo, String tableName, StringBuilder columnSql, List<TypeConvertor<?>> typeConvertors, StringBuilder joinedSql) {
        for (JoinInfo joinInfo : tableInfo.getJoinInfos()) {
            String slaveTableName;
            String aliasSlaveTable = joinInfo.getAliasSlaveTable();
            String slaveTableColumn;
            TableInfo joinedTableInfo;
            boolean needAs = StringUtils.isNotNullOrEmpty(aliasSlaveTable);
            if (joinInfo.isNotNested()) {
                joinedTableInfo = TableInfoManager.getTableInfo(tableInfo.getMappedClazz().getName() + " - " + joinInfo.getSlaveTableName());
                if (!needAs) {
                    aliasSlaveTable = joinInfo.getSlaveTableName();
                }
                slaveTableName = joinInfo.getSlaveTableName();
                slaveTableColumn = joinInfo.getSlaveTableColumn();
            } else {
                joinedTableInfo = TableInfoManager.getTableInfo(joinInfo.getSlaveTable());
                if (!needAs) {
                    aliasSlaveTable = joinedTableInfo.getTableName();
                }
                slaveTableName = joinedTableInfo.getTableName();
                slaveTableColumn = joinedTableInfo.getColumnInfo(joinInfo.getSlaveTableField()).getColumnName();
            }
            handleColumns(joinedTableInfo, joinedTableInfo.getTableName(), columnSql, typeConvertors, Collections.singletonList(joinedTableInfo.getColumnInfo(joinInfo.getSlaveTableField())));
            String joinedType = joinInfo.getJoinType().toString();
            String onSql = tableName +
                    "." + tableInfo.getColumnInfo(joinInfo.getMasterTableField()).getColumnName() +
                    " = " +
                    aliasSlaveTable + "." + slaveTableColumn;
            String postSql = "";
            if (needAs)
                postSql = SqlPool.SPACE + SqlPool.AS + SqlPool.SPACE + aliasSlaveTable;
            joinedSql.append(SqlCreator.create(SqlPool.JOIN_ON, joinedType, slaveTableName + postSql, onSql));
        }

    }

    /**
     * Instantiation a rowSql
     *
     * @param sqlType
     * @param params
     * @param columnSql
     * @param typeConvertors
     * @param fromSql
     * @return
     */
    private RowSql instantiationRowSql(SqlType sqlType, List<List<Object>> params, StringBuilder columnSql, List<TypeConvertor<?>> typeConvertors, StringBuilder fromSql, String otherSql) {
        RowSql rowSql = new RowSql();
        rowSql.setSqlType(sqlType);
        rowSql.setNativeSql(SqlCreator.create(SqlPool.QUERY, columnSql.substring(0, columnSql.length() - 1), fromSql.toString(), otherSql));
        rowSql.setParams(params);
        rowSql.setTypeConvertors(typeConvertors);
        return rowSql;
    }

    /**
     * Handle columns
     *
     * @param tableInfo
     * @param tableName
     * @param columnSql
     * @param typeConvertors
     * @param excludedColumns
     */
    private void handleColumns(TableInfo tableInfo, String tableName, StringBuilder columnSql, List<TypeConvertor<?>> typeConvertors, List<ColumnInfo> excludedColumns) {
        if (tableInfo.hasPk() && isInclude(tableInfo.getPkColumn(), excludedColumns)) {
            columnSql.append(tableName).append(".").append(tableInfo.getPkColumn().getColumnName()).append(",");
        }
        for (ColumnInfo columnInfo : tableInfo.getColumnMappedMap().values()) {
            if (isInclude(columnInfo, excludedColumns)) {
                columnSql.append(tableName).append(".").append(columnInfo.getColumnName()).append(",");
            }
        }
    }

    /**
     * 判断是否包含该列
     *
     * @param columnInfo
     * @param excludedColumns
     * @return
     */
    private boolean isInclude(ColumnInfo columnInfo, List<ColumnInfo> excludedColumns) {
        if (excludedColumns == null)
            return true;
        for (ColumnInfo excludedColumn : excludedColumns) {
            if (excludedColumn != null && excludedColumn.getColumnName().equals(columnInfo.getColumnName())) {
                return false;
            }
        }
        return true;
    }
}
