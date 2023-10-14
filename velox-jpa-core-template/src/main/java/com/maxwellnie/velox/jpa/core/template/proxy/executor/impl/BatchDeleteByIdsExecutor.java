package com.maxwellnie.velox.jpa.core.template.proxy.executor.impl;

import com.maxwellnie.velox.jpa.core.enums.QueryCondition;
import com.maxwellnie.velox.jpa.core.exception.EntityObjectException;
import com.maxwellnie.velox.jpa.core.exception.OpenStatementException;
import com.maxwellnie.velox.jpa.core.exception.PrimaryKeyException;
import com.maxwellnie.velox.jpa.core.template.sql.DeleteStatement;
import com.maxwellnie.velox.jpa.core.template.sql.WhereFragment;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.jdbc.transaction.Transaction;
import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.velox.jpa.core.template.sql.condition.NormalConditionFragment;
import com.maxwellnie.velox.jpa.core.template.proxy.executor.BaseUpdateExecutor;
import com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

/**
 * DaoImpl.batchDeleteByIds()方法的执行器，返回每个操作产生的影响。
 *
 * @author Maxwell Nie
 */
public class BatchDeleteByIdsExecutor extends BaseUpdateExecutor {
    private static Logger logger = LoggerFactory.getLogger(BatchDeleteByIdsExecutor.class);

    @Override
    public Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object, Object> cache, String daoImplHashCode, Object[] args) {
        if (!tableInfo.hasPk())
            throw new PrimaryKeyException(tableInfo.getColumnMappedMap() + " is not has primary key");
        try {
            /**
             * 开始执行sql语句
             */
            StatementWrapper statement = openStatement(tableInfo, context, cache, daoImplHashCode, args);
            Object result = executeSql(statement);
            flushCache(result, null, cache, context.getDirtyManager(), !context.getAutoCommit());
            return result;
        } catch (SQLException e) {
            logger.error("The sql error :"+e.getMessage()+"\t\n"+e.getCause());
            return new int[0];
        }
    }

    /**
     * 获取DeleteStatement对象
     *
     * @param tableInfo
     * @return
     */
    private DeleteStatement getDeleteStatement(TableInfo tableInfo) {
        /**
         * 新建DeleteStatement对象
         */
        DeleteStatement deleteStatement = new DeleteStatement();
        /**
         * 设置表名
         */
        deleteStatement.setTableName(tableInfo.getTableName());
        WhereFragment whereFragment = new WhereFragment();
        whereFragment.addConditionFragment(new NormalConditionFragment(tableInfo.getPkColumn().getColumnName(), QueryCondition.EQUAL, ""));
        deleteStatement.setWhereFragment(whereFragment);
        return deleteStatement;
    }

    protected StatementWrapper openStatement(TableInfo tableInfo, JdbcContext context, Cache<Object, Object> cache, String daoImplHashCode, Object[] args) {
        /**
         * 获取事务
         */
        Transaction transaction = context.getTransaction();
        /**
         * ids
         */
        Collection<Serializable> ids = args[0] == null ? null : (Collection<Serializable>) args[0];
        if (ids == null || ids.isEmpty())
            throw new EntityObjectException("The ids is null.");
        /**
         * 获取DeleteStatement这个类是一个delete语句的抽象类。
         */
        DeleteStatement deleteStatement = getDeleteStatement(tableInfo);
        /**
         * 整合资源
         */
        deleteStatement.integratingResource();
        /**
         * 获取sql
         */
        String sql = deleteStatement.getNativeSql();
        logger.debug("batchDeleteByIds() - sql:" + sql);
        PreparedStatement preparedStatement;
        try {
            /**
             * 预处理
             */
            preparedStatement = transaction.getConnection().prepareStatement(sql);
            /**
             * 设置参数
             */
            for (Serializable serializable : ids) {
                preparedStatement.setObject(1, serializable);
                preparedStatement.addBatch();
            }
            return new StatementWrapper(preparedStatement);
        } catch (SQLException e) {
            throw new OpenStatementException(e.getCause());
        }
    }

    protected Object executeSql(StatementWrapper statementWrapper) throws SQLException {
        int[] rows;
        try (Statement statement = statementWrapper.getStatement()) {
            rows = statement.executeBatch();
        }
        return rows;
    }
}
