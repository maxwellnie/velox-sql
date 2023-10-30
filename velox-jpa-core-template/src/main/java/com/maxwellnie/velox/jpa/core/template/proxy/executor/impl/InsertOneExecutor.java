package com.maxwellnie.velox.jpa.core.template.proxy.executor.impl;

import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.primary.PrimaryKeyStrategy;
import com.maxwellnie.velox.jpa.core.jdbc.table.primary.keyselector.JdbcSelector;
import com.maxwellnie.velox.jpa.core.manager.KeyStrategyManager;
import com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper;
import com.maxwellnie.velox.jpa.framework.exception.ExecutorException;
import com.maxwellnie.velox.jpa.framework.proxy.executor.insert.BaseInsertExecutor;
import com.maxwellnie.velox.jpa.framework.sql.SimpleSqlFragment;
import com.maxwellnie.velox.jpa.framework.utils.ExecutorUtils;
import com.maxwellnie.velox.jpa.framework.utils.StatementUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class InsertOneExecutor extends BaseInsertExecutor {
    private static final Logger logger= LoggerFactory.getLogger(InsertOneExecutor.class);
    public InsertOneExecutor() {
        super(logger, 0);
    }

    @Override
    protected void checkArgs(Object[] args) throws ExecutorException {
        if (args == null || args.length != 1){
            throw new ExecutorException("Method of args is empty.");
        }else {
            if(args[0] == null){
                throw new ExecutorException("Entity instance must be not null.");
            }
        }
    }

    @Override
    protected PreparedStatement doOpenStatement(Connection connection,TableInfo tableInfo, String sql) throws SQLException {
        if(KeyStrategyManager.getPrimaryKeyStrategy(tableInfo.getPkColumn().getStrategyName()).getKeySelector() instanceof JdbcSelector)
            return connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        else
            return connection.prepareStatement(sql);
    }

    @Override
    protected void doAfterOpenStatement(StatementWrapper statementWrapper, List<Object> params, Object[] args) throws SQLException {
        PreparedStatement preparedStatement=statementWrapper.getPrepareStatement();
        Object[] entityInstances = new Object[]{args[0]};
        TableInfo tableInfo= ExecutorUtils.of(statementWrapper, "tableInfo");
        PrimaryKeyStrategy keyStrategy= KeyStrategyManager.getPrimaryKeyStrategy(tableInfo.getPkColumn().getStrategyName());
        statementWrapper.getMetaData().addProperty("keyStrategy", keyStrategy);
        statementWrapper.getMetaData().addProperty("entityInstances",entityInstances);
        statementWrapper.setMode(StatementWrapper.UPDATE);
        try {
            setPrimaryKeyFromGeneratedKey(keyStrategy,entityInstances,tableInfo);
            StatementUtils.setParam(params, preparedStatement);
        } catch (IllegalAccessException e) {
            throw new ExecutorException(e);
        }
    }

    @Override
    protected void doBuildInsertSql(SimpleSqlFragment insertSql, List<ColumnInfo> columns, Object[] args, TableInfo tableInfo) throws ExecutorException {
        Object entityInstance=args[0];
        StringBuffer insertStr=new StringBuffer("INSERT INTO ")
                .append(tableInfo.getTableName()).append(" (");
        for (ColumnInfo columnInfo:columns){
            insertStr.append(columnInfo.getColumnName()).append(",");
        }
        insertStr.deleteCharAt(insertStr.length()-1).append(")")
                .append(" VALUES(");
        for (ColumnInfo columnInfo:columns){
            try {
                insertStr.append("?").append(",");
                insertSql.addParam(columnInfo.getColumnMappedField().get(entityInstance));
            } catch (IllegalAccessException e) {
                throw new ExecutorException(e);
            }
        }
        insertSql.setNativeSql(insertStr.deleteCharAt(insertStr.length()-1).append(");").toString());
    }
}
