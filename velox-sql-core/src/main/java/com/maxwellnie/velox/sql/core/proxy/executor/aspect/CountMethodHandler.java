package com.maxwellnie.velox.sql.core.proxy.executor.aspect;

import com.maxwellnie.velox.sql.core.meta.MetaData;
import com.maxwellnie.velox.sql.core.natives.dao.SqlDecorator;
import com.maxwellnie.velox.sql.core.natives.exception.ExecutorException;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSession;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlPool;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.row.RowSql;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfo;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Maxwell Nie
 */
public class CountMethodHandler extends AbstractMethodHandler {
    public CountMethodHandler() {
        super(999, new MethodAspect[]{
                new MethodAspect("buildRowSql", new Class[]{
                        MetaData.class
                }),
                new MethodAspect("openStatement", new Class[]{
                        RowSql.class,
                        JdbcSession.class,
                        TableInfo.class,
                        Object[].class
                })
        }, new TargetMethodSignature("count", new Class[]{SqlDecorator.class}));
    }

    @Override
    public Object handle(SimpleInvocation simpleInvocation) {
        if (simpleInvocation.getArgs().length == 1) {
            MetaData metaData = (MetaData) simpleInvocation.getArgs()[0];
            SqlDecorator<?> sqlDecorator = metaData.getProperty("sqlDecorator");
            if (sqlDecorator != null) {
                sqlDecorator.setLimitFragment(null);
            }
            try {
                return simpleInvocation.targetMethod.invoke(simpleInvocation.getTarget(), metaData);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ExecutorException(e);
            }
        } else {
            RowSql rowSql = (RowSql) simpleInvocation.getArgs()[0];
            String sql = rowSql.getNativeSql();
            TableInfo tableInfo = (TableInfo) simpleInvocation.getArgs()[2];
            int fromIndex = sql.indexOf("FROM");
            sql = sql.substring(fromIndex);
            String count = "COUNT(*)";
            if (tableInfo.hasPk()) {
                count = "COUNT(" + tableInfo.getTableName() + "." + tableInfo.getPkColumn().getColumnName() + ")";
            }
            sql = "SELECT" + SqlPool.SPACE + count + SqlPool.SPACE + sql;
            rowSql.setNativeSql(sql);
            try {
                return simpleInvocation.targetMethod.invoke(simpleInvocation.getTarget(), simpleInvocation.getArgs());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ExecutorException(e);
            }
        }
    }
}
