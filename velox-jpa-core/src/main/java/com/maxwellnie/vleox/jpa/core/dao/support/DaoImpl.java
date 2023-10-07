package com.maxwellnie.vleox.jpa.core.dao.support;

import com.maxwellnie.vleox.jpa.core.annotation.RegisterMethod;
import com.maxwellnie.vleox.jpa.core.proxy.executor.impl.*;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * 数据访问器，与数据库交互，操作数据库中数据
 *
 * @param <T>
 * @author Maxwell Nie
 */
public interface DaoImpl<T> {
    /**
     * 添加一条数据
     *
     * @param t
     * @return
     * @throws SQLException
     */
    @RegisterMethod(InsertExecutor.class)
    public int insert(T t) throws SQLException;

    /**
     * 批量添加
     *
     * @param collection
     * @return
     * @throws SQLException
     */
    @RegisterMethod(BatchInsertExecutor.class)
    public int[] batchInsert(Collection<T> collection) throws SQLException;

    /**
     * 修改一条数据
     *
     * @param sqlBuilder
     * @return
     * @throws SQLException
     */
    @RegisterMethod(UpdateExecutor.class)
    public int update(T t, SqlBuilder<T> sqlBuilder) throws SQLException;

    /**
     * 删除一条数据
     *
     * @param sqlBuilder
     * @return
     * @throws SQLException
     */
    @RegisterMethod(DeleteExecutor.class)
    public int delete(SqlBuilder<T> sqlBuilder) throws SQLException;

    /**
     * 批量删除
     *
     * @param collection
     * @return
     * @throws SQLException
     */
    @RegisterMethod(BatchDeleteByIdsExecutor.class)
    public int[] batchDeleteByIds(Collection<Serializable> collection) throws SQLException;

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
     * @throws SQLException
     */
    @RegisterMethod(QueryAllExecutor.class)
    public List<T> queryAll(SqlBuilder<T> sqlBuilder) throws SQLException;

    /**
     * 获取表数据长度
     *
     * @return
     * @throws SQLException
     */
    @RegisterMethod(SizeExecutor.class)
    public long size(SqlBuilder<T> sqlBuilder) throws SQLException;
}
