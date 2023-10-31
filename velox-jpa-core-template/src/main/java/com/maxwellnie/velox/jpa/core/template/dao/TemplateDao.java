package com.maxwellnie.velox.jpa.core.template.dao;

import com.maxwellnie.velox.jpa.core.annotation.DaoImplDeclared;
import com.maxwellnie.velox.jpa.core.annotation.RegisterMethod;
import com.maxwellnie.velox.jpa.core.template.proxy.executor.impl.DeleteOneExecutor;
import com.maxwellnie.velox.jpa.core.template.proxy.executor.impl.InsertOneExecutor;
import com.maxwellnie.velox.jpa.core.template.proxy.executor.impl.QueryExecutor;
import com.maxwellnie.velox.jpa.core.template.proxy.executor.impl.UpdateOneExecutor;
import com.maxwellnie.velox.jpa.framework.proxy.executor.FrameworkDaoImplRegister;
import com.maxwellnie.velox.jpa.framework.sql.SqlBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Maxwell Nie
 */
@DaoImplDeclared(FrameworkDaoImplRegister.class)
public interface TemplateDao<T> {
    /**
     * 添加一条数据
     *
     * @param t
     * @return
     */
    @RegisterMethod(InsertOneExecutor.class)
    int insert(T t);

    /**
     * 修改一条数据
     *
     * @param sqlBuilder
     * @return
     */
    @RegisterMethod(UpdateOneExecutor.class)
    int update(T t, SqlBuilder<T> sqlBuilder);

    /**
     * 删除一条数据
     *
     * @param sqlBuilder
     * @return
     */
    @RegisterMethod(DeleteOneExecutor.class)
    int delete(SqlBuilder<T> sqlBuilder);

    /**
     * 查询数据
     *
     * @param sqlBuilder
     * @return
     * @throws SQLException
     */
    default T queryOne(SqlBuilder<T> sqlBuilder) {
        List<T> result = queryAll(sqlBuilder);
        if (result != null && !result.isEmpty())
            return result.get(0);
        else
            return null;
    }

    /**
     * 查询全部数据
     *
     * @return
     */
    @RegisterMethod(QueryExecutor.class)
    List<T> queryAll(SqlBuilder<T> sqlBuilder);
}
