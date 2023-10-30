package com.maxwellnie.velox.jpa.framework.proxy.executor.delete;

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
import java.util.List;

/**
 * @author Maxwell Nie
 */
public abstract class BaseDeleteExecutor extends BaseExecutor {
    public BaseDeleteExecutor(Logger logger, Object errorResult) {
        super(logger, errorResult);
    }

    @Override
    protected SimpleSqlFragment getNativeSql(Object[] args, TableInfo tableInfo) throws ExecutorException {
        SimpleSqlFragment deleteSql = new SimpleSqlFragment();
        doBuildDeleteSql(deleteSql, null, args, tableInfo);
        return deleteSql;
    }

    protected abstract void doBuildDeleteSql(SimpleSqlFragment deleteSql, List<ColumnInfo> columns, Object[] args, TableInfo tableInfo);

    @Override
    protected SqlResult executeSql(StatementWrapper statementWrapper, SimpleSqlFragment sqlFragment, String daoImplHashCode, Cache<Object,Object> cache) throws ExecutorException {
        try (PreparedStatement preparedStatement = statementWrapper.getPrepareStatement()) {
            Object result = doExecuteSql(preparedStatement,statementWrapper.getMode());
            return new SqlResult(CLEAR_FLAG, result, null);
        } catch (SQLException e) {
            logger.error(ErrorUtils.getExceptionLog(e, sqlFragment.getNativeSql(), sqlFragment.getParams()));
            throw new ExecutorException("SQL error!");
        }
    }
}
