package com.maxwellnie.velox.jpa.framework.proxy.executor.query;

import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.cahce.key.CacheKey;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper;
import com.maxwellnie.velox.jpa.core.utils.jdbc.ResultSetUtils;
import com.maxwellnie.velox.jpa.framework.exception.ExecutorException;
import com.maxwellnie.velox.jpa.framework.proxy.executor.BaseExecutor;
import com.maxwellnie.velox.jpa.framework.sql.SimpleSqlFragment;
import com.maxwellnie.velox.jpa.framework.utils.ErrorUtils;
import com.maxwellnie.velox.jpa.framework.utils.ExecutorUtils;
import org.slf4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * 基本的查询执行器
 *
 * @author Maxwell Nie
 */
public abstract class BaseQueryExecutor extends BaseExecutor {
    public BaseQueryExecutor(Logger logger, Object errorResult) {
        super(logger, errorResult);
    }

    @Override
    protected SimpleSqlFragment getNativeSql(Object[] args, TableInfo tableInfo) throws ExecutorException {
        SimpleSqlFragment selectSql = new SimpleSqlFragment();
        List<ColumnInfo> columns = new LinkedList<>();
        if (tableInfo.hasPk())
            columns.add(tableInfo.getPkColumn());
        columns.addAll(tableInfo.getColumnMappedMap().values());
        doBuildSelectSql(selectSql, columns, args, tableInfo);
        return selectSql;
    }

    protected abstract void doBuildSelectSql(SimpleSqlFragment sqlFragment, List<ColumnInfo> columns, Object[] args, TableInfo tableInfo);

    @Override
    protected SqlResult executeSql(StatementWrapper statementWrapper, SimpleSqlFragment sqlFragment, String daoImplHashCode, Cache<Object,Object> cache) throws ExecutorException {
        TableInfo tableInfo = ExecutorUtils.of(statementWrapper, "tableInfo");
        CacheKey cacheKey = ExecutorUtils.of(statementWrapper, "cacheKey");
        cacheKey.setDaoImplHashCode(daoImplHashCode);
        try (PreparedStatement preparedStatement = statementWrapper.getPrepareStatement()) {
            List result= (List) cache.get(cacheKey);
            if(result==null){
                ResultSet resultSet = preparedStatement.executeQuery();
                result= ResultSetUtils.convertEntity(resultSet, tableInfo);
                resultSet.close();
            }else
                logger.debug("Cache Hit.");
            return new SqlResult(FLUSH_FLAG, result, cacheKey);
        } catch (SQLException e) {
            logger.error(ErrorUtils.getExceptionLog(e, sqlFragment.getNativeSql(), sqlFragment.getParams()));
            throw new ExecutorException("SQL error!");
        }
    }
}
