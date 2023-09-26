package com.crazy.sql.core.proxy.executor.impl;

import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.config.GlobalConfig;
import com.crazy.sql.core.enums.PrimaryMode;
import com.crazy.sql.core.exception.EntityObjectException;
import com.crazy.sql.core.proxy.executor.Executor;
import com.crazy.sql.core.jdbc.table.primary.PrimaryKeyStrategy;
import com.crazy.sql.core.jdbc.table.primary.generator.Generator;
import com.crazy.sql.core.jdbc.table.primary.keyselector.KeySelector;
import com.crazy.sql.core.manager.KeyStrategyManager;
import com.crazy.sql.core.jdbc.sql.InsertStatement;
import com.crazy.sql.core.jdbc.table.column.ColumnInfo;
import com.crazy.sql.core.jdbc.table.TableInfo;
import com.crazy.sql.core.jdbc.context.JdbcContext;
import com.crazy.sql.core.jdbc.transaction.Transaction;
import com.crazy.sql.core.utils.jdbc.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * Accessor.insert()方法执行器，执行insert()方法返回受影响行数。<br/>
 * @author Akiba no ichiichiyoha
 */
public class InsertExecutor implements Executor {
    private static final Logger logger= LoggerFactory.getLogger(InsertExecutor.class);
    @Override
    public Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object,Object> cache, String accessorHashCode, Object[] args) {
        /**
         * insert(Object o)这里的o是参数0的地址，也就是实体类对象地址。
         */
        Object o= args[0];
        if(o==null)
            throw new EntityObjectException("The entity object "+tableInfo.getMappedClazz() +" is null.");
        /**
         * 获取事务
         */
        Transaction transaction=context.getTransaction();
        /**
         * 如果开启缓存，清空缓存
         */
        if(GlobalConfig.getInstance().isCache())
            context.getDirtyManager().get(cache).clear();
        /**
         * 接下来是获取主键策略的过程
         * 通过判断TableInfo携带的主键策略模式获取主键策略的key
         */
        //-------------------------------------Begin-------------------------------------------------
        String strategyKey;
        if(!tableInfo.hasPk())
            strategyKey=KeyStrategyManager.DEFAULT;
        else {
            PrimaryMode mode=tableInfo.getPkColumn().getPrimaryMode();
            if (mode.equals(PrimaryMode.NONE))
                strategyKey=KeyStrategyManager.DEFAULT;
            else if(mode.equals(PrimaryMode.JDBC_AUTO))
                strategyKey=KeyStrategyManager.JDBC_AUTO;
            else
                strategyKey=tableInfo.getPkColumn().getStrategyName();
        }
        /**
         * 获取主键策略
         */
        PrimaryKeyStrategy primaryKeyStrategy=KeyStrategyManager.getPrimaryKeyStrategy(strategyKey);
        //--------------------------------------End-------------------------------------------------
        try {
            /**
             * 通过策略获取主键生成器和主键查询器
             */
            Generator generator =primaryKeyStrategy.getKeyGenerator();
            KeySelector keySelector=primaryKeyStrategy.getKeySelector();
            /**
             * 开始执行sql语句
             */
            return openStatement(transaction.getConnection(), tableInfo,o,generator,keySelector,strategyKey);
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 这个方法可以执行insert操作的sql语句，通过传递的参数拼接sql然后执行，如果是设置了自增主键，那么将会把主键值设置到实体类对象o上。
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
    private int openStatement(Connection connection, TableInfo tableInfo,Object o, Generator generator, KeySelector selector,String generateMode) throws SQLException, IllegalAccessException {
        /**
         * 获取是否开启了JDBC_AUTO
         */
        boolean jdbcAuto=generateMode.equals(KeyStrategyManager.JDBC_AUTO);
        /**
         * 获取InsertStatement这个类是一个insert语句的抽象类。
         */
        InsertStatement insertStatement=getInsertStatement(tableInfo,o,jdbcAuto);
        PreparedStatement preparedStatement=null;
        int row=0;
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
            if(jdbcAuto){
                insertStatement.integratingResource();
                String sql=insertStatement.getNativeSql();
                preparedStatement=connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
                logger.debug("insert() - sql:"+sql);
                JdbcUtils.setParam(insertStatement,preparedStatement);
                row=preparedStatement.executeUpdate();
                Object primaryKeys=selector.selectGeneratorKey(preparedStatement);
                if(primaryKeys instanceof Object[]){
                    Object[] objects=(Object[])primaryKeys;
                    if(objects.length!=0)
                        tableInfo.getPkColumn().getColumnMappedField().set(o,objects[0]);
                }
            }else if(generateMode.equals(KeyStrategyManager.DEFAULT)){
                insertStatement.integratingResource();
                String sql=insertStatement.getNativeSql();
                logger.debug("insert() - sql:"+sql);
                preparedStatement=connection.prepareStatement(sql);
                JdbcUtils.setParam(insertStatement,preparedStatement);
                row=preparedStatement.executeUpdate();
            }else{
                Object key=generator.nextKey();
                insertStatement.integratingResource();
                String sql=insertStatement.getNativeSql();
                logger.debug("insert() - sql:"+sql);
                preparedStatement=connection.prepareStatement(sql);
                JdbcUtils.setParam(insertStatement,preparedStatement);
                row=preparedStatement.executeUpdate();
                Object primary=selector.selectGeneratorKey(key);
                tableInfo.getPkColumn().getColumnMappedField().set(o,primary);
            }
            //--------------------------------------End-------------------------------------------------
        }catch (Exception e){
            throw e;
        }finally {
            /**
             * 释放资源
             */
            if(preparedStatement!=null)
                preparedStatement.close();
        }
        return row;
    }

    /**
     * 通过TableInfo和实体对象o获取InsertStatement对象
     * @param tableInfo
     * @param o
     * @param jdbcAuto
     * @return
     * @throws IllegalAccessException
     */
    private InsertStatement getInsertStatement(TableInfo tableInfo,Object o,boolean jdbcAuto) throws IllegalAccessException {
        InsertStatement insertStatement=new InsertStatement();
        insertStatement.setTableName(tableInfo.getTableName());
        /**
         * 如果处于JDBC_AUTO模式，那么不把主键列添加到insert语句中且不会把主键值作为param添加到PrepareStatement
         */
        if (tableInfo.hasPk()&&!jdbcAuto){
            insertStatement.getColumns().add(tableInfo.getPkColumn().getColumnName());
            insertStatement.getValues().add(tableInfo.getPkColumn().getColumnMappedField().get(o));
        }
        /**
         * 把列添加到insert语句中且把属性值作为param添加到PrepareStatement
         */
        for (Map.Entry<String, ColumnInfo> entry:tableInfo.getColumnMappedMap().entrySet()) {
            insertStatement.getColumns().add(entry.getValue().getColumnName());
            insertStatement.getValues().add(entry.getValue().getColumnMappedField().get(o));
        }
        return insertStatement;
    }


}
