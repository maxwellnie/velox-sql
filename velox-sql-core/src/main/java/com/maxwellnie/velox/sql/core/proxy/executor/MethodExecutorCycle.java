package com.maxwellnie.velox.sql.core.proxy.executor;

import com.maxwellnie.velox.sql.core.cache.Cache;
import com.maxwellnie.velox.sql.core.cache.key.CacheKey;
import com.maxwellnie.velox.sql.core.meta.MetaData;
import com.maxwellnie.velox.sql.core.natives.exception.ExecutorException;
import com.maxwellnie.velox.sql.core.natives.jdbc.mapping.ReturnTypeMapping;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSession;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlType;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.row.RowSql;
import com.maxwellnie.velox.sql.core.natives.jdbc.statement.StatementWrapper;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfo;
import com.maxwellnie.velox.sql.core.natives.task.TaskQueue;
import com.maxwellnie.velox.sql.core.proxy.executor.aspect.AbstractMethodHandler;
import com.maxwellnie.velox.sql.core.proxy.executor.aspect.MethodHandler;
import com.maxwellnie.velox.sql.core.proxy.executor.aspect.SimpleInvocation;
import com.maxwellnie.velox.sql.core.proxy.executor.result.SqlResult;
import com.maxwellnie.velox.sql.core.utils.common.MetaWrapperUtils;
import com.maxwellnie.velox.sql.core.utils.common.SystemClock;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Maxwell Nie
 */
public class MethodExecutorCycle {
    public static Object start(MethodExecutor executor, Logger logger, TableInfo tableInfo, JdbcSession context, Cache cache, String daoImplHashCode, ReturnTypeMapping returnTypeMapping, Object[] args) throws ExecutorException {
        executor.check(tableInfo, context, args);
        // 元数据
        MetaData metaData = executor.prepared(tableInfo, args);
        // 构建SQL
        RowSql rowSql = executor.buildRowSql(metaData);
        if (rowSql != null) {
            long startTime = SystemClock.now();
            // 打开Statement
            StatementWrapper statementWrapper = executor.openStatement(rowSql, context, tableInfo, args);
            // 绑定元数据
            statementWrapper.getMetaData().addFromMetaData(metaData);
            logger.debug("SQL ### : " + rowSql.getNativeSql());
            logger.debug("PARAM # : " + rowSql.getParams());
            // 缓存Key
            CacheKey cacheKey = new CacheKey(tableInfo.getMappedClazz(), rowSql.getNativeSql(), daoImplHashCode);
            // 缓存Key添加参数
            for (List<Object> params : rowSql.getParams())
                cacheKey.addParams(params);
            // 缓存Key添加Statement
            statementWrapper.addProperty("cacheKey", cacheKey);
            AtomicReference<Object> result = new AtomicReference<>();
            TaskQueue taskQueue = context.getTaskQueue();
            if (rowSql.getSqlType().equals(SqlType.QUERY) && cache != null) {
                result.set(cache.get(cacheKey));
                if (result.get() != null) {
                    logger.debug("Cache hit.");
                    return result.get();
                } else if(taskQueue != null) {
                    taskQueue.require(daoImplHashCode, cacheKey, () -> {
                        if (rowSql.getSqlType().equals(SqlType.QUERY) && cache != null) {
                            result.set(cache.get(cacheKey));
                            if (result.get() == null) {
                                result.set(executor.runSql(statementWrapper, rowSql));
                                result.set(handleResult(executor, logger, tableInfo, context, cache, returnTypeMapping, startTime, statementWrapper, result));
                            }else
                                logger.debug("Cache hit.");
                        } else {
                            result.set(executor.runSql(statementWrapper, rowSql));
                            result.set(handleResult(executor, logger, tableInfo, context, cache, returnTypeMapping, startTime, statementWrapper, result));
                        }
                    });
                    return result.get();
                }else
                    result.set(executor.runSql(statementWrapper, rowSql));
            }else
                result.set(executor.runSql(statementWrapper, rowSql));
            return handleResult(executor, logger, tableInfo, context, cache, returnTypeMapping, startTime, statementWrapper, result);
        }
        throw new ExecutorException("rowSql build failed,rowSql is null.");
    }

    private static Object handleResult(MethodExecutor executor, Logger logger, TableInfo tableInfo, JdbcSession context, Cache cache, ReturnTypeMapping returnTypeMapping, long startTime, StatementWrapper statementWrapper, AtomicReference<Object> result) {
        if (result.get() != null) {
            SqlResult sqlResult = executor.handleRunnerResult(result.get(), tableInfo, MetaWrapperUtils.of(statementWrapper, "cacheKey"), returnTypeMapping);
            logger.debug("SQL EXECUTED | TIME: " + (SystemClock.now() - startTime) + "ms");
            executor.flushCache(sqlResult, cache, context.getDirtyManager(), !context.getAutoCommit());
            executor.closeStatement(statementWrapper);
            return sqlResult.getResult();
        }
        executor.closeStatement(statementWrapper);
        throw new ExecutorException("The result of sql running is null.That's wrong result.");
    }

    public static class CycleMethodExecutorHandler extends AbstractMethodHandler {
        public CycleMethodExecutorHandler() {
            super(MethodHandler.SPRING_SUPPORT_INDEX - 1L, new MethodAspect[]{
                    new MethodAspect(
                            "execute", new Class<?>[]{
                            TableInfo.class,
                            JdbcSession.class,
                            Cache.class,
                            String.class,
                            ReturnTypeMapping.class,
                            Object[].class,
                    }
                    )
            }, TargetMethodSignature.ANY);
        }

        @Override
        public Object handle(SimpleInvocation simpleInvocation) {
            Object[] args = simpleInvocation.getArgs();
            // 1.获取表信息
            TableInfo tableInfo = (TableInfo) args[0];
            // 2.获取spring jdbc context
            JdbcSession springJdbcSession = (JdbcSession) args[1];
            // 3.获取 cache
            Cache cache = (Cache) args[2];
            // 4.获取 dao 实现类 hashcode
            String hashcode = (String) args[3];
            // 5.获取返回类型映射
            ReturnTypeMapping returnTypeMapping = (ReturnTypeMapping) args[4];
            // 6.获取 dao 实现类方法参数
            Object[] methodArgs = (Object[]) args[5];
            MethodExecutor methodExecutor = (MethodExecutor) simpleInvocation.getTarget();
            return MethodExecutorCycle.start((MethodExecutor) simpleInvocation.getProxy(), methodExecutor.getLogger(), tableInfo, springJdbcSession, cache, hashcode, returnTypeMapping, methodArgs);
        }
    }
}
