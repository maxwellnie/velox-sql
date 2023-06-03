package com.crazy.sql.core.executor;

import com.crazy.sql.core.query.QueryWord;

import java.sql.SQLException;
import java.util.List;

public interface SQLExecutor<T> {
    /**
     * 添加一条数据
     * @param t
     * @return
     * @throws SQLException
     */
    public int insert(T t) throws SQLException;

    /**
     * 修改一条数据
     * @param t
     * @return
     * @throws SQLException
     */
    public int update(T t) throws SQLException;

    /**
     * 删除一条数据
     * @param t
     * @return
     * @throws SQLException
     */
    public int delete(T t) throws SQLException;

    /**
     * 查询一条数据
     * @param t
     * @return
     * @throws SQLException
     */
    public T queryOne(T t) throws SQLException;

    /**
     * 查询全部数据
     * @return
     * @throws SQLException
     */
    public List<T> queryAll() throws SQLException;

    /**
     * 条件查询
     * @param queryWords
     * @return
     * @throws SQLException
     */
    public List<T> queryByWords(QueryWord... queryWords)throws SQLException;
}
