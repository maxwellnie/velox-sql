package com.maxwellnie.velox.jpa.core.template.proxy.executor.impl;

import com.maxwellnie.velox.jpa.core.enums.PrimaryMode;
import com.maxwellnie.velox.jpa.core.exception.EntityObjectException;
import com.maxwellnie.velox.jpa.core.jdbc.sql.InsertStatement;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.jpa.core.jdbc.transaction.Transaction;
import com.maxwellnie.velox.jpa.core.manager.KeyStrategyManager;
import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.velox.jpa.core.jdbc.table.primary.PrimaryKeyStrategy;
import com.maxwellnie.velox.jpa.core.jdbc.table.primary.generator.Generator;
import com.maxwellnie.velox.jpa.core.jdbc.table.primary.keyselector.KeySelector;
import com.maxwellnie.velox.jpa.core.proxy.executor.BaseUpdateExecutor;
import com.maxwellnie.velox.jpa.core.utils.jdbc.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * DaoImpl.insert()方法执行器，执行insert()方法返回受影响行数。<br/>
 *
 * @author Maxwell Nie
 */
public class InsertExecutor extends BaseUpdateExecutor {
    private static final Logger logger = LoggerFactory.getLogger(InsertExecutor.class);

    @Override
    public Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object, Object> cache, String daoImplHashCode, Object[] args) {
        Object o = args[0];
        if (o == null) {
            throw new EntityObjectException("The entity object " + tableInfo.getMappedClazz() + " is null.");
        }

        Transaction transaction = context.getTransaction();

        String strategyKey;
        if (!tableInfo.hasPk()) {
            strategyKey = KeyStrategyManager.DEFAULT;
        } else {
            strategyKey = tableInfo.getPkColumn().getStrategyName();
        }

        PrimaryKeyStrategy primaryKeyStrategy = KeyStrategyManager.getPrimaryKeyStrategy(strategyKey);

        try {
            Generator generator = primaryKeyStrategy.getKeyGenerator();
            KeySelector keySelector = primaryKeyStrategy.getKeySelector();

            Object result = openStatement(transaction.getConnection(), tableInfo, o, generator, keySelector, strategyKey);

            flushCache(result, null, cache, context.getDirtyManager(), !context.getAutoCommit());

            return result;
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 这个方法可以执行insert操作的sql语句，通过传递的参数拼接sql然后执行，如果是设置了自增主键，那么将会把主键值设置到实体类对象o上。
     *
     * @param connection
     * @param tableInfo
     * @param o
     * @param generator
     * @param selector
     * @param generateMode
     * @return
     * @throws SQLException
     * @throws IllegalAccessException
     */
    private int openStatement(Connection connection, TableInfo tableInfo, Object o, Generator generator, KeySelector selector, String generateMode) throws SQLException, IllegalAccessException {
        /**
         * 获取是否开启了JDBC_AUTO
         */
        boolean jdbcAuto = generateMode.equals(KeyStrategyManager.JDBC_AUTO);
        /**
         * 获取InsertStatement这个类是一个insert语句的抽象类。
         */
        InsertStatement insertStatement = null;
        PreparedStatement preparedStatement = null;
        int row = 0;
        try {
            /**
             * 接下来是执行添加sql的过程
             * 分为三种模式：
             * <p>
             * 如果是JDBC_AUTO模式，那么会设置connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
             * 使得执行后可以使用JdbcKeySelector来获取主键值，并且设置到实体类对象中。
             * <p>
             * 如果是DEFAULT模式，那么正常执行sql。
             * <p>
             * 如果是自定义模式，那么将使用自定义的Generator生成主键值，使用自定义的KeySelector查询主键值，这里一般情况下使用
             * GeneratorSelector来查询主键值，这个方法是把Generator生成的主键值作为参数传递给GeneratorSelector，GeneratorSelector
             * 直接将主键值返回，并且设置到实体类。
             */
            //-------------------------------------Begin-------------------------------------------------
            if (jdbcAuto) {
                insertStatement=getInsertStatement(tableInfo, o, true);
                insertStatement.integratingResource();
                String sql = insertStatement.getNativeSql();
                preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                logger.debug("insert() - sql:" + sql);
                JdbcUtils.setParam(insertStatement, preparedStatement);
                row = preparedStatement.executeUpdate();
                Object primaryKeys = selector.selectGeneratorKey(preparedStatement);
                if (primaryKeys instanceof Object[]) {
                    Object[] objects = (Object[]) primaryKeys;
                    if (objects.length != 0)
                        tableInfo.getPkColumn().getColumnMappedField().set(o, objects[0]);
                }
            } else if (generateMode.equals(KeyStrategyManager.DEFAULT)) {
                insertStatement=getInsertStatement(tableInfo, o, false);
                insertStatement.integratingResource();
                String sql = insertStatement.getNativeSql();
                logger.debug("insert() - sql:" + sql);
                preparedStatement = connection.prepareStatement(sql);
                JdbcUtils.setParam(insertStatement, preparedStatement);
                row = preparedStatement.executeUpdate();
            } else {
                Object key = generator.nextKey();
                tableInfo.getPkColumn().getColumnMappedField().set(o, key);
                insertStatement=getInsertStatement(tableInfo, o, true);
                insertStatement.integratingResource();
                String sql = insertStatement.getNativeSql();
                logger.debug("insert() - sql:" + sql);
                preparedStatement = connection.prepareStatement(sql);
                JdbcUtils.setParam(insertStatement, preparedStatement);
                row = preparedStatement.executeUpdate();
            }
            //--------------------------------------End-------------------------------------------------
        } finally {
            /**
             * 释放资源
             */
            if (preparedStatement != null)
                preparedStatement.close();
        }
        return row;
    }

    /**
     * 通过TableInfo和实体对象o获取InsertStatement对象
     *
     * @param tableInfo
     * @param o
     * @param jdbcAuto
     * @return
     * @throws IllegalAccessException
     */
    private InsertStatement getInsertStatement(TableInfo tableInfo, Object o, boolean jdbcAuto) throws IllegalAccessException {
        InsertStatement insertStatement = new InsertStatement();
        insertStatement.setTableName(tableInfo.getTableName());
        /**
         * 如果处于JDBC_AUTO模式，那么不把主键列添加到insert语句中且不会把主键值作为param添加到PrepareStatement
         */
        if (tableInfo.hasPk() && !jdbcAuto) {
            insertStatement.getColumns().add(tableInfo.getPkColumn().getColumnName());
            insertStatement.getValues().add(tableInfo.getPkColumn().getColumnMappedField().get(o));
        }
        /**
         * 把列添加到insert语句中且把属性值作为param添加到PrepareStatement
         */
        for (Map.Entry<String, ColumnInfo> entry : tableInfo.getColumnMappedMap().entrySet()) {
            insertStatement.getColumns().add(entry.getValue().getColumnName());
            insertStatement.getValues().add(entry.getValue().getColumnMappedField().get(o));
        }
        return insertStatement;
    }
}
