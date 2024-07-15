package com.maxwellnie.velox.sql.core.natives.dao;

import com.maxwellnie.velox.sql.core.annotation.DaoImplDeclared;
import com.maxwellnie.velox.sql.core.annotation.RegisterMethod;
import com.maxwellnie.velox.sql.core.proxy.executor.impl.DeleteMethodExecutor;
import com.maxwellnie.velox.sql.core.proxy.executor.impl.InsertMethodExecutor;
import com.maxwellnie.velox.sql.core.proxy.executor.impl.QueryMethodExecutor;
import com.maxwellnie.velox.sql.core.proxy.executor.impl.UpdateMethodExecutor;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
    default T selectOne(SqlDecorator<T> sqlDecorator){
        List<T> list = select(sqlDecorator);
        return Optional.ofNullable(list).map(ts -> ts.size() > 0 ? ts.get(0) : null).orElse(null);
    }
    default List<T> selectAll(){
        return select(null);
    }
    @RegisterMethod(QueryMethodExecutor.class)
    long count(SqlDecorator<T> sqlDecorator);

    @RegisterMethod(QueryMethodExecutor.class)
    Page<T> selectPage(Page<T> page, SqlDecorator<T> sqlDecorator);
    default Page<T> selectFirstPage(SqlDecorator<T> sqlDecorator){
        return selectPage(null,sqlDecorator);
    }
}
