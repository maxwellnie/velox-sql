package com.maxwellnie.velox.jpa.framework.proxy.executor.insert;

import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.primary.PrimaryKeyStrategy;
import com.maxwellnie.velox.jpa.core.jdbc.table.primary.generator.KeyGenerator;
import com.maxwellnie.velox.jpa.core.jdbc.table.primary.generator.NoKeyGenerator;
import com.maxwellnie.velox.jpa.core.jdbc.table.primary.keyselector.JdbcSelector;
import com.maxwellnie.velox.jpa.core.jdbc.table.primary.keyselector.KeySelector;
import com.maxwellnie.velox.jpa.core.jdbc.table.primary.keyselector.NoKeySelector;
import com.maxwellnie.velox.jpa.core.manager.KeyStrategyManager;
import com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper;
import com.maxwellnie.velox.jpa.framework.exception.ExecutorException;
import com.maxwellnie.velox.jpa.framework.proxy.executor.BaseExecutor;
import com.maxwellnie.velox.jpa.framework.sql.SimpleSqlFragment;
import com.maxwellnie.velox.jpa.framework.utils.ErrorUtils;
import com.maxwellnie.velox.jpa.framework.utils.ExecutorUtils;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public abstract class BaseInsertExecutor extends BaseExecutor {
    public BaseInsertExecutor(Logger logger, Object errorResult) {
        super(logger, errorResult);
    }

    @Override
    protected SimpleSqlFragment getNativeSql(Object[] args, TableInfo tableInfo) throws ExecutorException {
        SimpleSqlFragment insertSql = new SimpleSqlFragment();
        List<ColumnInfo> columns = new LinkedList<>();
        if (!tableInfo.hasPk())
            columns.add(tableInfo.getPkColumn());
        columns.addAll(tableInfo.getColumnMappedMap().values());
        doBuildInsertSql(insertSql, columns, args, tableInfo);
        return insertSql;
    }

    /**
     * 构建Sql语句
     *
     * @param insertSql
     * @param columns
     * @param args
     * @param tableInfo
     * @throws ExecutorException
     */
    protected void doBuildInsertSql(SimpleSqlFragment insertSql, List<ColumnInfo> columns, Object[] args, TableInfo tableInfo) throws ExecutorException {
        StringBuffer insertStr = new StringBuffer("INSERT INTO ")
                .append(tableInfo.getTableName()).append(" (");
        for (ColumnInfo columnInfo : columns) {
            insertStr.append(columnInfo.getColumnName()).append(",");
        }
        insertStr.deleteCharAt(insertStr.length() - 1).append(")")
                .append(" VALUES(");
        for (ColumnInfo columnInfo : columns) {
            insertStr.append("?").append(",");
        }
        insertSql.setNativeSql(insertStr.deleteCharAt(insertStr.length() - 1).append(");").toString());
    }

    @Override
    protected PreparedStatement doOpenStatement(Connection connection, TableInfo tableInfo, String sql) throws SQLException {
        if (KeyStrategyManager.getPrimaryKeyStrategy(tableInfo.getPkColumn().getStrategyName()).getKeySelector() instanceof JdbcSelector)
            return connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        else
            return connection.prepareStatement(sql);
    }

    @Override
    protected SqlResult executeSql(StatementWrapper statementWrapper, SimpleSqlFragment sqlFragment, String daoImplHashCode, Cache<Object, Object> cache) throws ExecutorException {
        PrimaryKeyStrategy keyStrategy = ExecutorUtils.of(statementWrapper, "keyStrategy");
        TableInfo tableInfo = ExecutorUtils.of(statementWrapper, "tableInfo");
        Object[] entityInstances = ExecutorUtils.of(statementWrapper, "entityInstances");
        try (PreparedStatement preparedStatement = statementWrapper.getPrepareStatement()) {
            Object result = doExecuteSql(preparedStatement, statementWrapper.getMode());
            setPrimaryKeyFormSelectedKey(keyStrategy, preparedStatement, result, entityInstances, tableInfo);
            return new SqlResult(CLEAR_FLAG, result, null);
        } catch (SQLException | IllegalAccessException e) {
            logger.error(ErrorUtils.getExceptionLog(e, sqlFragment.getNativeSql(), sqlFragment.getParams()));
            throw new ExecutorException("SQL error!");
        }
    }

    @Override
    protected void doAfterOpenStatement(StatementWrapper statementWrapper, List<Object> params, Object[] args) throws SQLException {
        try {
            TableInfo tableInfo = ExecutorUtils.of(statementWrapper, "tableInfo");
            PrimaryKeyStrategy keyStrategy = ExecutorUtils.of(statementWrapper, "keyStrategy");
            Object[] entityInstances = ExecutorUtils.of(statementWrapper, "entityInstances");
            setPrimaryKeyFromGeneratedKey(keyStrategy, entityInstances, tableInfo);
            statementWrapper.setMode(StatementWrapper.UPDATE);
        } catch (IllegalAccessException e) {
            throw new ExecutorException(e);
        }
    }

    protected void setPrimaryKeyFromGeneratedKey(PrimaryKeyStrategy keyStrategy, Object[] entityInstances, TableInfo tableInfo) throws IllegalAccessException {
        if (tableInfo.hasPk()) {
            KeyGenerator keyGenerator = keyStrategy.getKeyGenerator();
            if (!(keyGenerator instanceof NoKeyGenerator)) {
                for (Object entityInstance : entityInstances) {
                    tableInfo.getPkColumn().getColumnMappedField().set(entityInstance, keyGenerator.nextKey());
                }
            }
        }
    }

    protected void setPrimaryKeyFormSelectedKey(PrimaryKeyStrategy keyStrategy, PreparedStatement preparedStatement, Object result, Object[] entityInstances, TableInfo tableInfo) throws IllegalAccessException {
        if (tableInfo.hasPk()) {
            KeySelector keySelector = keyStrategy.getKeySelector();
            if (keySelector instanceof NoKeySelector)
                return;
            Object primaryKeys = keySelector.selectGeneratorKey(preparedStatement, result);
            if (primaryKeys != null) {
                if (primaryKeys instanceof Object[]) {
                    Object[] objects = (Object[]) primaryKeys;
                    if (objects.length != 0 && objects.length == entityInstances.length)
                        for (int index = 0; index < entityInstances.length; index++)
                            tableInfo.getPkColumn().getColumnMappedField().set(entityInstances[index], objects[index]);
                }
            }
        }
    }

}
