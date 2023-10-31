package com.maxwellnie.velox.jpa.framework.proxy.executor.cycle;

import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.proxy.executor.Executor;
import com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper;
import com.maxwellnie.velox.jpa.framework.exception.ExecutorException;
import com.maxwellnie.velox.jpa.framework.sql.SimpleSqlFragment;
import com.maxwellnie.velox.jpa.framework.utils.ErrorUtils;
import org.slf4j.Logger;

import java.sql.Connection;

/**
 * 代理了Executor，在不同的阶段委派ExecuteCycle的不同方法来执行并获取结果。
 *
 * @author Maxwell Nie
 */
public class ExecutorDelegate implements Executor {
    private final ExecuteCycle concrete;
    private final Logger logger;
    private final Object errorResult;

    public ExecutorDelegate(ExecuteCycle concrete) {
        this.concrete = concrete;
        this.logger = concrete.getLogger();
        this.errorResult = concrete.errorResult;
    }

    /**
     * 所有的Executor都应该遵循这个规范。
     *
     * @param tableInfo
     * @param context
     * @param cache
     * @param daoImplHashCode
     * @param args
     * @return
     */
    @Override
    public Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object, Object> cache, String daoImplHashCode, Object[] args) {
        try {
            /**
             * 检查环境阶段。
             */
            concrete.checkExecuteCondition(tableInfo, context, cache, daoImplHashCode, args);
            /**
             * 获取连接阶段。
             */
            Connection connection = concrete.doConnection(context);
            /**
             * sql创建阶段。
             */
            SimpleSqlFragment sqlFragment = concrete.getNativeSql(args, tableInfo);
            logger.debug("SQL ### : " + sqlFragment.getNativeSql());
            logger.debug("PARAM # : " + sqlFragment.getParams());
            /**
             * Statement实例化阶段。
             */
            StatementWrapper statementWrapper = concrete.openStatement(sqlFragment, connection, tableInfo, args);
            /**
             * sql执行阶段。
             */
            long startTime = System.currentTimeMillis();
            ExecuteCycle.SqlResult sqlResult = concrete.executeSql(statementWrapper, sqlFragment, daoImplHashCode, cache);
            logger.debug("SQL EXECUTED | TIME: " + (System.currentTimeMillis() - startTime) + "ms.");
            /**
             * 缓存刷新阶段。
             */
            concrete.flushCache(sqlResult, cache, context.getDirtyManager(), !context.getAutoCommit());
            return sqlResult.getResult();
        } catch (ExecutorException e) {
            logger.error(ErrorUtils.getSimpleExceptionLog(e));
            return errorResult;
        }
    }
}
