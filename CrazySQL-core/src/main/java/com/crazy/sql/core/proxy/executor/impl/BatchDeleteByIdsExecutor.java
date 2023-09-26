package com.crazy.sql.core.proxy.executor.impl;

import com.crazy.sql.core.config.GlobalConfig;
import com.crazy.sql.core.enums.QueryCondition;
import com.crazy.sql.core.exception.EntityObjectException;
import com.crazy.sql.core.exception.PrimaryKeyException;
import com.crazy.sql.core.jdbc.sql.WhereFragment;
import com.crazy.sql.core.jdbc.sql.condition.NormalConditionFragment;
import com.crazy.sql.core.proxy.executor.Executor;
import com.crazy.sql.core.jdbc.sql.DeleteStatement;
import com.crazy.sql.core.jdbc.table.TableInfo;
import com.crazy.sql.core.jdbc.context.JdbcContext;
import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.jdbc.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Accessor.batchDeleteByIds()方法的执行器，返回每个操作产生的影响。
 * @author Akiba no ichiichiyoha
 */
public class BatchDeleteByIdsExecutor implements Executor {
    private static Logger logger= LoggerFactory.getLogger(BatchDeleteByIdsExecutor.class);
    @Override
    public Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object,Object> cache, String accessorHashCode, Object[] args) {
        if(!tableInfo.hasPk())
            throw new PrimaryKeyException(tableInfo.getColumnMappedMap()+" is not has primary key");
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
         * ids
         */
        Collection<Serializable> ids= args[0]==null?null: (Collection<Serializable>) args[0];
        if(ids==null||ids.isEmpty())
            throw new EntityObjectException("The ids is null.");
        try {
            /**
             * 开始执行sql语句
             */
            return openStatement(transaction.getConnection(), tableInfo,ids);
        } catch (SQLException e) {
            e.printStackTrace();
            return new int[0];
        }
    }
    private int[] openStatement(Connection connection, TableInfo tableInfo, Collection<Serializable> ids) throws SQLException {
        /**
         * 获取DeleteStatement这个类是一个delete语句的抽象类。
         */
        DeleteStatement deleteStatement=getDeleteStatement(tableInfo);
        /**
         * 整合资源
         */
        deleteStatement.integratingResource();
        /**
         * 获取sql
         */
        String sql=deleteStatement.getNativeSql();
        logger.debug("batchDeleteByIds() - sql:"+sql);
        PreparedStatement preparedStatement=null;
        int[] rows;
        try{
            /**
             * 预处理
             */
            preparedStatement=connection.prepareStatement(sql);
            /**
             * 设置参数
             */
            for (Serializable serializable:ids){
                preparedStatement.setObject(1,serializable);
                preparedStatement.addBatch();
            }
            rows=preparedStatement.executeBatch();
        } catch (Exception e){
            throw e;
        }finally {
            if(preparedStatement!=null)
                preparedStatement.close();
        }
        return rows;
    }

    /**
     * 获取DeleteStatement对象
     * @param tableInfo
     * @return
     */
    private DeleteStatement getDeleteStatement(TableInfo tableInfo) {
        /**
         * 新建DeleteStatement对象
         */
        DeleteStatement deleteStatement=new DeleteStatement();
        /**
         * 设置表名
         */
        deleteStatement.setTableName(tableInfo.getTableName());
        WhereFragment whereFragment=new WhereFragment();
        whereFragment.addConditionFragment(new NormalConditionFragment(tableInfo.getPkColumn().getColumnName(), QueryCondition.EQUAL,""));
        deleteStatement.setWhereFragment(whereFragment);
        return deleteStatement;
    }
}
