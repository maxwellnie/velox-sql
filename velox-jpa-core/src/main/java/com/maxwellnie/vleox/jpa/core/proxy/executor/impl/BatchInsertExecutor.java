package com.maxwellnie.vleox.jpa.core.proxy.executor.impl;

import com.maxwellnie.vleox.jpa.core.cahce.Cache;
import com.maxwellnie.vleox.jpa.core.enums.PrimaryMode;
import com.maxwellnie.vleox.jpa.core.exception.EntityObjectException;
import com.maxwellnie.vleox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.vleox.jpa.core.jdbc.sql.InsertStatement;
import com.maxwellnie.vleox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.vleox.jpa.core.jdbc.table.column.ColumnInfo;
import com.maxwellnie.vleox.jpa.core.jdbc.table.primary.PrimaryKeyStrategy;
import com.maxwellnie.vleox.jpa.core.jdbc.table.primary.generator.Generator;
import com.maxwellnie.vleox.jpa.core.jdbc.table.primary.keyselector.KeySelector;
import com.maxwellnie.vleox.jpa.core.jdbc.transaction.Transaction;
import com.maxwellnie.vleox.jpa.core.manager.KeyStrategyManager;
import com.maxwellnie.vleox.jpa.core.utils.jdbc.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * DaoImpl.batchInsert()方法的执行器，返回每个操作产生的影响。
 *
 * @author Maxwell Nie
 */
public class BatchInsertExecutor extends BaseUpdateExecutor {
    private static final Logger logger = LoggerFactory.getLogger(BatchInsertExecutor.class);

    @Override
    public Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object, Object> cache, String daoImplHashCode, Object[] args) {
        /**
         * batchInsert(Collection collection)这里的collection是参数0的地址，也就是实体类对象集合地址。
         */
        Collection<?> collection = args[0] == null ? null : (Collection<?>) args[0];
        if (collection == null || collection.isEmpty())
            throw new EntityObjectException("The entity objects " + tableInfo.getMappedClazz() + " is null.");
        /**
         * 获取事务
         */
        Transaction transaction = context.getTransaction();
        /**
         * 接下来是获取主键策略的过程
         * 通过判断TableInfo携带的主键策略模式获取主键策略的key
         */
        //-------------------------------------Begin-------------------------------------------------
        String strategyKey;
        if (!tableInfo.hasPk())
            strategyKey = KeyStrategyManager.DEFAULT;
        else {
            PrimaryMode mode = tableInfo.getPkColumn().getPrimaryMode();
            if (mode.equals(PrimaryMode.NONE))
                strategyKey = KeyStrategyManager.DEFAULT;
            else if (mode.equals(PrimaryMode.JDBC_AUTO))
                strategyKey = KeyStrategyManager.JDBC_AUTO;
            else
                strategyKey = tableInfo.getPkColumn().getStrategyName();
        }
        /**
         * 获取主键策略
         */
        PrimaryKeyStrategy primaryKeyStrategy = KeyStrategyManager.getPrimaryKeyStrategy(strategyKey);
        //--------------------------------------End-------------------------------------------------
        try {
            /**
             * 通过策略获取主键生成器和主键查询器
             */
            Generator generator = primaryKeyStrategy.getKeyGenerator();
            KeySelector keySelector = primaryKeyStrategy.getKeySelector();
            /**
             * 开始执行sql语句
             */
            Object result = openStatement(transaction.getConnection(), tableInfo, collection, generator, keySelector, strategyKey);
            flushCache(result, null, cache, context.getDirtyManager(), !context.getAutoCommit());
            return result;
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 这个方法可以执行批量insert操作的sql语句，通过传递的参数拼接sql然后执行，如果是设置了自增主键，那么将会把主键值设置到实体类对象o上。
     *
     * @param connection
     * @param tableInfo
     * @param generator
     * @param selector
     * @param generateMode
     * @return
     * @throws SQLException
     * @throws IllegalAccessException
     */
    private int[] openStatement(Connection connection, TableInfo tableInfo, Collection collection, Generator generator, KeySelector selector, String generateMode) throws SQLException, IllegalAccessException {
        /**
         * 获取是否开启了JDBC_AUTO
         */
        boolean jdbcAuto = generateMode.equals(KeyStrategyManager.JDBC_AUTO);
        /**
         * 获取InsertStatement这个类是一个insert语句的抽象类。
         */
        InsertStatement insertStatement = getInsertStatement(tableInfo, jdbcAuto);
        PreparedStatement preparedStatement = null;
        int[] rows;
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
                insertStatement.integratingResource();
                String sql = insertStatement.getNativeSql();
                preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                logger.debug("batchInsert() - sql:" + sql);
                rows = handle(collection, preparedStatement, tableInfo, insertStatement, true);
                selectKey(tableInfo, collection, selector, preparedStatement);
            } else if (generateMode.equals(KeyStrategyManager.DEFAULT)) {
                insertStatement.integratingResource();
                String sql = insertStatement.getNativeSql();
                logger.debug("batchInsert() - sql:" + sql);
                preparedStatement = connection.prepareStatement(sql);
                rows = handle(collection, preparedStatement, tableInfo, insertStatement, false);
            } else {
                List keys = new LinkedList();
                for (Object o : collection) {
                    keys.add(generator.nextKey());
                }
                insertStatement.integratingResource();
                String sql = insertStatement.getNativeSql();
                logger.debug("batchInsert() - sql:" + sql);
                preparedStatement = connection.prepareStatement(sql);
                rows = handle(collection, preparedStatement, tableInfo, insertStatement, false);
                selectKey(tableInfo, collection, selector, keys.toArray());
            }
            //--------------------------------------End-------------------------------------------------
        } finally {
            /**
             * 释放资源
             */
            if (preparedStatement != null)
                preparedStatement.close();
        }
        return rows;
    }

    private void selectKey(TableInfo tableInfo, Collection collection, KeySelector selector, Object param) throws IllegalAccessException {
        Object primaryKeys = selector.selectGeneratorKey(param);
        if (primaryKeys instanceof Object[]) {
            Object[] objects = (Object[]) primaryKeys;
            if (objects.length != 0) {
                int index = 0;
                for (Object o : collection) {
                    tableInfo.getPkColumn().getColumnMappedField().set(o, objects[index]);
                    index++;
                }
            }
        }
    }

    private int[] handle(Collection collection, PreparedStatement preparedStatement, TableInfo tableInfo, InsertStatement insertStatement, boolean auto) throws SQLException, IllegalAccessException {
        int[] rows;
        for (Object o : collection) {
            List list = new LinkedList();
            if (tableInfo.hasPk() && !auto)
                list.add(tableInfo.getPkColumn().getColumnMappedField().get(o));
            for (ColumnInfo columnInfo : tableInfo.getColumnMappedMap().values())
                list.add(columnInfo.getColumnMappedField().get(o));
            list.addAll(insertStatement.getValues());
            JdbcUtils.setParam(list, preparedStatement);
            preparedStatement.addBatch();
        }
        JdbcUtils.setParam(insertStatement, preparedStatement);
        rows = preparedStatement.executeBatch();
        return rows;
    }

    /**
     * 通过TableInfo和实体对象o获取InsertStatement对象
     *
     * @param tableInfo
     * @param jdbcAuto
     * @return
     */
    private InsertStatement getInsertStatement(TableInfo tableInfo, boolean jdbcAuto) {
        InsertStatement insertStatement = new InsertStatement();
        insertStatement.setTableName(tableInfo.getTableName());
        /**
         * 如果处于JDBC_AUTO模式，那么不把主键列添加到insert语句中且不会把主键值作为param添加到PrepareStatement
         */
        if (tableInfo.hasPk() && !jdbcAuto) {
            insertStatement.getColumns().add(tableInfo.getPkColumn().getColumnName());
        }
        /**
         * 把列添加到insert语句中且把属性值作为param添加到PrepareStatement
         */
        for (Map.Entry<String, ColumnInfo> entry : tableInfo.getColumnMappedMap().entrySet()) {
            insertStatement.getColumns().add(entry.getValue().getColumnName());
        }
        return insertStatement;
    }
}
