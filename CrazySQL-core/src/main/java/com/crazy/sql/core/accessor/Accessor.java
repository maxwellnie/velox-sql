package com.crazy.sql.core.accessor;

import com.crazy.sql.core.accessor.page.DataPage;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * 数据访问器，与数据库交互，操作数据库中数据
 * @param <T>
 * @author Akiba no ichiichiyoha
 */
public interface Accessor<T> {
    /**
     * 添加一条数据
     * @param t
     * @return
     * @throws SQLException
     */
    public int insert(T t) throws SQLException;

    /**
     * 批量添加
     * @param collection
     * @return
     * @throws SQLException
     */
    public int[] batchInsert(Collection<T> collection) throws SQLException;
    /**
     * 修改一条数据
     * @param sqlBuilder
     * @return
     * @throws SQLException
     */
    public int update(T t, SqlBuilder<T> sqlBuilder) throws SQLException;
    /**
     * 删除一条数据
     * @param sqlBuilder
     * @return
     * @throws SQLException
     */
    public int delete(SqlBuilder<T> sqlBuilder) throws SQLException;
    /**
     * 批量删除
     * @param collection
     * @return
     * @throws SQLException
     */
    public int[] batchDeleteByIds(Collection<Serializable> collection) throws SQLException;
    /**
     * 查询数据
     * @param sqlBuilder
     * @return
     * @throws SQLException
     */
    public default T queryOne(SqlBuilder<T> sqlBuilder) throws SQLException{
        List<T> result=queryAll(sqlBuilder);
        if(result!=null&&!result.isEmpty())
            return result.get(0);
        else
            return null;
    }
    /**
     * 查询全部数据
     * @return
     * @throws SQLException
     */
    public List<T> queryAll(SqlBuilder<T> sqlBuilder) throws SQLException;
    /**
     * 获取表数据长度
     * @return
     * @throws SQLException
     */
    public long size(SqlBuilder<T> sqlBuilder) throws SQLException;
}
