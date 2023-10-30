package com.maxwellnie.velox.jpa.framework.proxy.executor.update;

import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper;
import com.maxwellnie.velox.jpa.framework.exception.ExecutorException;
import com.maxwellnie.velox.jpa.framework.proxy.executor.BaseExecutor;
import com.maxwellnie.velox.jpa.framework.sql.SimpleSqlFragment;
import com.maxwellnie.velox.jpa.framework.utils.ErrorUtils;
import org.slf4j.Logger;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public abstract class BaseUpdateExecutor extends BaseExecutor {
    public BaseUpdateExecutor(Logger logger, Object errorResult) {
        super(logger, errorResult);
    }

    @Override
    protected SimpleSqlFragment getNativeSql(Object[] args, TableInfo tableInfo) throws ExecutorException {
        SimpleSqlFragment updateSql = new SimpleSqlFragment();
        List<ColumnInfo> columns = new LinkedList<>(tableInfo.getColumnMappedMap().values());
        doBuildUpdateSql(updateSql, columns, args, tableInfo);
        return updateSql;
    }

    protected abstract void doBuildUpdateSql(SimpleSqlFragment updateSql, List<ColumnInfo> columns, Object[] args, TableInfo tableInfo);

    @Override
    protected SqlResult executeSql(StatementWrapper statementWrapper, SimpleSqlFragment sqlFragment, String daoImplHashCode, Cache<Object,Object> cache) throws ExecutorException {
        try (PreparedStatement preparedStatement = statementWrapper.getPrepareStatement()) {
            Object result = doExecuteSql(preparedStatement, statementWrapper.getMode());
            return new SqlResult(CLEAR_FLAG, result, null);
        } catch (SQLException e) {
            logger.error(ErrorUtils.getExceptionLog(e, sqlFragment.getNativeSql(), sqlFragment.getParams()));
            throw new ExecutorException("SQL error!");
        }
    }
}
