package com.maxwellnie.velox.sql.core.proxy.executor.aspect;

import com.maxwellnie.velox.sql.core.natives.dao.BaseSql;
import com.maxwellnie.velox.sql.core.natives.exception.ExecutorException;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSession;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlPool;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.row.RowSql;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfo;
import com.maxwellnie.velox.sql.core.natives.type.convertor.ConvertorManager;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;

/**
 * @author Maxwell Nie
 */
public class LastSqlMethodHandler extends AbstractMethodHandler {
    public LastSqlMethodHandler() {
        super(1, new MethodAspect[]{
                new MethodAspect("openStatement", new Class[]{
                        RowSql.class,
                        JdbcSession.class,
                        TableInfo.class,
                        Object[].class
                })
        }, TargetMethodSignature.ANY);
    }

    @Override
    public Object handle(SimpleInvocation simpleInvocation) {
        try {
            RowSql rowSql = (RowSql) simpleInvocation.getArgs()[0];
            if (rowSql.getSqlDecorator() != null && rowSql.getSqlDecorator().getLastSql() != null) {
                BaseSql sql = rowSql.getSqlDecorator().getLastSql();
                rowSql.setNativeSql(rowSql.getNativeSql() + SqlPool.SPACE + sql.getSql());
                rowSql.getTypeConvertors().addAll(rowSql.getParams().stream().map((o) -> ConvertorManager.getConvertor(o.getClass())).collect(Collectors.toList()));
                rowSql.getParams().stream().peek((params) -> params.addAll(sql.getParams()));
            }
            return simpleInvocation.proceed();
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new ExecutorException(e);
        }
    }
}
