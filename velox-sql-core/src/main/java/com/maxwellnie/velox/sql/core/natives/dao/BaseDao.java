package com.maxwellnie.velox.sql.core.natives.dao;

import com.maxwellnie.velox.sql.core.annotation.DaoImplDeclared;
import com.maxwellnie.velox.sql.core.annotation.RegisterMethod;
import com.maxwellnie.velox.sql.core.proxy.executor.impl.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author Maxwell Nie
 */
@DaoImplDeclared
public interface BaseDao<T> {
    @RegisterMethod(InsertMethodExecutor.class)
    int insert(T t);
    @RegisterMethod(DeleteMethodExecutor.class)
    int delete(SqlDecorator<T> sqlDecorator);
    @RegisterMethod(UpdateMethodExecutor.class)
    int update(T t, SqlDecorator<T> sqlDecorator);
    @RegisterMethod(InsertMethodExecutor.class)
    int[] batchInsert(Collection<T> t);
    @RegisterMethod(DeleteMethodExecutor.class)
    int[] batchDelete(Serializable[] ids);
    @RegisterMethod(QueryMethodExecutor.class)
    List<T> select(SqlDecorator<T> sqlDecorator);
    @RegisterMethod(QueryMethodExecutor.class)
    long count(SqlDecorator<T> sqlDecorator);
    @RegisterMethod(QueryMethodExecutor.class)
    Page<T> selectPage(Page<T> page, SqlDecorator<T> sqlDecorator);
}
