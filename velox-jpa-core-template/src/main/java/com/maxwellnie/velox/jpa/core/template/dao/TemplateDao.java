package com.maxwellnie.velox.jpa.core.template.dao;

import com.maxwellnie.velox.jpa.core.annotation.RegisterMethod;
import com.maxwellnie.velox.jpa.core.template.proxy.executor.impl.*;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public interface TemplateDao<T> {
    /**
     * 添加一条数据
     *
     * @param t
     * @return
     */
    @RegisterMethod(InsertExecutor.class)
    public int insert(T t);

    /**
     * 批量添加
     *
     * @param collection
     * @return
     */
    @RegisterMethod(BatchInsertExecutor.class)
    public int[] batchInsert(Collection<T> collection);

    /**
     * 修改一条数据
     *
     * @param sqlBuilder
     * @return
     */
    @RegisterMethod(UpdateExecutor.class)
    public int update(T t, SqlBuilder<T> sqlBuilder);

    /**
     * 删除一条数据
     *
     * @param sqlBuilder
     * @return
     */
    @RegisterMethod(DeleteExecutor.class)
    public int delete(SqlBuilder<T> sqlBuilder);

    /**
     * 批量删除
     *
     * @param collection
     * @return
     */
    @RegisterMethod(BatchDeleteByIdsExecutor.class)
    public int[] batchDeleteByIds(Collection<Serializable> collection);

    /**
     * 查询数据
     *
     * @param sqlBuilder
     * @return
     * @throws SQLException
     */
    public default T queryOne(SqlBuilder<T> sqlBuilder) throws SQLException {
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
    @RegisterMethod(QueryAllExecutor.class)
    public List<T> queryAll(SqlBuilder<T> sqlBuilder);

    /**
     * 获取表数据长度
     *
     * @return
     */
    @RegisterMethod(SizeExecutor.class)
    public long size(SqlBuilder<T> sqlBuilder);
}
