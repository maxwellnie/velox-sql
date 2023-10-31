package com.maxwellnie.velox.jpa.core.proxy.executor.wrapper;

import com.maxwellnie.velox.jpa.core.utils.reflect.meta.MetaData;

import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * {@link java.sql.Statement}和{@link java.sql.PreparedStatement}的包装器，用于Executor某些执行周期的信息传递。
 *
 * @author Maxwell Nie
 */
public class StatementWrapper {
    /**
     * 查询模式
     */
    public static final int QUERY = -1;
    /**
     * 批处理模式
     */
    public static final int BATCH = 1;
    /**
     * 修改模式
     */
    public static final int UPDATE = 2;
    /**
     * Statement对象
     */
    private Statement statement;
    /**
     * 可转换为某个Java对象的元数据
     */
    private MetaData metaData;
    /**
     * Statement的目标模式：查询、修改、批处理
     *
     * @see PreparedStatement
     */
    private int mode;

    public StatementWrapper(Statement statement, MetaData metaData, int mode) {
        this.statement = statement;
        this.metaData = metaData;
        this.mode = mode;
    }

    public StatementWrapper(Statement statement) {
        this.statement = statement;
        this.metaData = MetaData.ofEmpty();
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * 获取元数据的键对应值，在Executor中不建议直接使用。<br/>
     * 最好使用框架模块的{@link ExecutorUtils.of(StatementWrapper,String)}。
     *
     * @param name
     * @param <T>
     * @return
     */
    public <T> T getProperty(String name) {
        return (T) this.metaData.getProperty(name);
    }

    public PreparedStatement getPrepareStatement() {
        if (statement instanceof PreparedStatement)
            return (PreparedStatement) this.statement;
        return null;
    }
}
