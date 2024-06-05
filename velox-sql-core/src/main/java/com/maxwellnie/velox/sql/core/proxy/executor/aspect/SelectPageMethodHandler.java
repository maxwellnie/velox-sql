package com.maxwellnie.velox.sql.core.proxy.executor.aspect;

import com.maxwellnie.velox.sql.core.cache.key.CacheKey;
import com.maxwellnie.velox.sql.core.config.simple.SingletonConfiguration;
import com.maxwellnie.velox.sql.core.meta.MetaData;
import com.maxwellnie.velox.sql.core.natives.dao.Page;
import com.maxwellnie.velox.sql.core.natives.dao.SqlDecorator;
import com.maxwellnie.velox.sql.core.natives.dao.VPage;
import com.maxwellnie.velox.sql.core.natives.exception.ClassTypeException;
import com.maxwellnie.velox.sql.core.natives.exception.ExecutorException;
import com.maxwellnie.velox.sql.core.natives.jdbc.mapping.ReturnTypeMapping;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSession;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlPool;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlType;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.row.QueryRowSqlFactory;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.row.RowSql;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.row.RowSqlFactory;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.runner.SqlExecutor;
import com.maxwellnie.velox.sql.core.natives.jdbc.statement.StatementWrapper;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfo;
import com.maxwellnie.velox.sql.core.proxy.executor.result.SqlResult;
import com.maxwellnie.velox.sql.core.utils.log.LogUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class SelectPageMethodHandler extends AbstractMethodHandler{
    public SelectPageMethodHandler() {
        super(999, new MethodAspect[]{
                new MethodAspect("prepared", new Class[]{
                        TableInfo.class,
                        Object[].class
                }),
                new MethodAspect("buildRowSql", new Class[]{
                        MetaData.class
                }),
                new MethodAspect("check", new Class[]{
                        TableInfo.class,
                        JdbcSession.class,
                        Object[].class
                }),
                new MethodAspect("openStatement", new Class[]{
                        RowSql.class,
                        JdbcSession.class,
                        TableInfo.class,
                        Object[].class
                }),
                new MethodAspect("runSql", new Class[]{
                        StatementWrapper.class,
                        RowSql.class
                }),
                new MethodAspect("handleRunnerResult", new Class[]{
                        Object.class,
                        TableInfo.class,
                        CacheKey.class,
                        ReturnTypeMapping.class
                })
        },  new TargetMethodSignature("selectPage", new Class[]{Page.class, SqlDecorator.class}));
    }

    @Override
    public Object handle(SimpleInvocation simpleInvocation) {
        try{
            switch (simpleInvocation.targetMethod.getName()) {
                case "check":
                    check((TableInfo) simpleInvocation.getArgs()[0], (JdbcSession) simpleInvocation.getArgs()[1], (Object[]) simpleInvocation.getArgs()[2], simpleInvocation);
                    return null;
                case "prepared":
                    return prepared((TableInfo) simpleInvocation.getArgs()[0], (Object[]) simpleInvocation.getArgs()[1]);
                case "buildRowSql":
                    return buildRowSql((MetaData) simpleInvocation.getArgs()[0]);
                case "openStatement":
                    return openStatement((RowSql) simpleInvocation.getArgs()[0], (JdbcSession) simpleInvocation.getArgs()[1], (TableInfo) simpleInvocation.getArgs()[2], (Object[]) simpleInvocation.getArgs()[3], simpleInvocation);
                case "runSql":
                    return runSql((StatementWrapper) simpleInvocation.getArgs()[0], (RowSql) simpleInvocation.getArgs()[1]);
                case "handleRunnerResult":
                    return handleRunnerResult(simpleInvocation.getArgs()[0], (TableInfo) simpleInvocation.getArgs()[1], (CacheKey) simpleInvocation.getArgs()[2], (ReturnTypeMapping) simpleInvocation.getArgs()[3]);
                default:
                    throw new ExecutorException("method not found" + simpleInvocation.getTargetMethod().getName());
            }
        }catch (Throwable e){
            throw new ExecutorException(e);
        }
    }
    public MetaData prepared(TableInfo tableInfo, Object[] args) throws ExecutorException {
        MetaData metaData = MetaData.ofEmpty();
        metaData.addProperty("tableInfo", tableInfo);
        metaData.addProperty("sqlType", SqlType.QUERY);
        metaData.addProperty("page", args[0]);
        metaData.addProperty("sqlDecorator", args[1]);
        return metaData;
    }

    public void check(TableInfo tableInfo, JdbcSession session, Object[] args,SimpleInvocation simpleInvocation) throws ExecutorException, InvocationTargetException, IllegalAccessException {
        simpleInvocation.proceed(tableInfo, session, args);
        if (args.length != 2) {
            throw new ExecutorException("args length must be contains Page and SqlDecorator parameter");
        }
    }

    public RowSql buildRowSql(MetaData metaData) throws ExecutorException {
        SqlDecorator<?> sqlDecorator = metaData.getProperty("sqlDecorator");
        if (sqlDecorator != null) {
            sqlDecorator.setLimitFragment(null);
        }
        RowSqlFactory rowSqlFactory = new QueryRowSqlFactory();
        return rowSqlFactory.getRowSql(metaData);
    }

    public StatementWrapper openStatement(RowSql rowSql, JdbcSession session, TableInfo tableInfo, Object[] args, SimpleInvocation simpleInvocation) throws ExecutorException, InvocationTargetException, IllegalAccessException {
        long count = 0;
        try {
            count = count(rowSql, tableInfo, session);
        } catch (SQLException|ClassTypeException e) {
            throw new ExecutorException(e);
        }
        Page<?> page = (Page<?>) args[0];
        long currentRequestCount = 0;
        long updatedCurrent = 0;
        long offset = 10;
        if(page != null && page.getCurrent() > 0){
            currentRequestCount = page.getCurrent() * page.getOffset();
            updatedCurrent = page.getCurrent();
            if(currentRequestCount > count){
                updatedCurrent = count / page.getOffset() - 1;
            }
        }
        rowSql = SingletonConfiguration.getInstance().getDialect().getDialectRowSql(rowSql, updatedCurrent, offset);
        StatementWrapper statementWrapper = (StatementWrapper) simpleInvocation.proceed(rowSql, session, tableInfo, args);
        statementWrapper.addProperty("total", count);
        statementWrapper.addProperty("current", updatedCurrent);
        statementWrapper.addProperty("offset", offset);
        return statementWrapper;
    }
    public long count(RowSql rowSql, TableInfo tableInfo, JdbcSession session) throws SQLException, ClassTypeException {
        String sql = rowSql.getNativeSql();
        int fromIndex = sql.indexOf("FROM");
        sql = sql.substring(fromIndex);
        String count = "COUNT(*)";
        if (tableInfo.hasPk()){
            count = "COUNT("+tableInfo.getTableName()+"."+tableInfo.getPkColumn().getColumnName()+")";
        }
        sql = "SELECT"+ SqlPool.SPACE +count + SqlPool.SPACE + sql;
        RowSql countRowSql = new RowSql();
        countRowSql.setNativeSql(sql);
        StatementWrapper statementWrapper = new StatementWrapper(session.getTransaction().getConnection().prepareStatement(countRowSql.getNativeSql()));
        PreparedStatement ps = statementWrapper.getPrepareStatement();
        try (ResultSet resultSet = ps.executeQuery()){
            if (resultSet.next())
                return resultSet.getLong(1);
            else
                return 0;
        }finally {
            ps.close();
        }

    }

    public Object runSql(StatementWrapper statementWrapper, RowSql rowSql) throws ExecutorException {
        SqlExecutor<?> sqlExecutor = SqlExecutor.get(rowSql.getSqlType());
        try {
            statementWrapper.addProperty("result", sqlExecutor.run(rowSql, statementWrapper));
            return statementWrapper;
        } catch (SQLException | ClassTypeException e) {
            throw LogUtils.convertToAdaptLoggerException(e, rowSql.getNativeSql(), rowSql.getParams());
        }
    }

    public SqlResult handleRunnerResult(Object result, TableInfo tableInfo, CacheKey cacheKey, ReturnTypeMapping returnTypeMapping) throws ExecutorException {
        Object entityObjects = Collections.EMPTY_LIST;
        StatementWrapper statementWrapper = (StatementWrapper) result;
        if (result != null) {
            try {
                entityObjects = tableInfo.getResultSetParser().parseResultSet((ResultSet) statementWrapper.getProperty("result"), returnTypeMapping);
            } catch (SQLException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                     IllegalAccessException e) {
                throw new ExecutorException(e);
            }
        }
        entityObjects = new VPage((List) entityObjects, (long) statementWrapper.getProperty("total"), (long) statementWrapper.getProperty("current"), (long) statementWrapper.getProperty("offset"));
        return new SqlResult(SqlResult.CacheFlush.FLUSH, entityObjects, cacheKey);
    }
}
