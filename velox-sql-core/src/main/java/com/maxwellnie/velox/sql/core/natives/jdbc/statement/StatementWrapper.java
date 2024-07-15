package com.maxwellnie.velox.sql.core.natives.jdbc.statement;

import com.maxwellnie.velox.sql.core.meta.MetaData;
import com.maxwellnie.velox.sql.core.natives.exception.ClassTypeException;
import com.maxwellnie.velox.sql.core.natives.wrapper.MetaStyleWrapper;

import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * {@link java.sql.Statement}的包装器，用于Executor某些执行周期的信息传递。
 *
 * @author Maxwell Nie
 */
public class StatementWrapper extends MetaStyleWrapper<Statement> implements AutoCloseable {
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
     * Statement的目标模式：查询、修改、批处理
     *
     * @see PreparedStatement
     */
    private int mode;

    public StatementWrapper(Statement statement, int mode) {
        this.t = statement;
        this.mode = mode;
    }

    public StatementWrapper(Statement statement) {
        this.t = statement;
    }

    public MetaData getMetaData() {
        return this.meta;
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public PreparedStatement getPrepareStatement() throws ClassTypeException {
        if (this.t instanceof PreparedStatement)
            return (PreparedStatement) this.t;
        throw new ClassTypeException("Statement Object [" + this.t + "] is not class[java.sql.PreparedStatement]");
    }

    @Override
    public void close() throws Exception {
        this.t.close();
    }
}
