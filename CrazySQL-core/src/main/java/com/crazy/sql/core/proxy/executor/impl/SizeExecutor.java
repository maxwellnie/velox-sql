package com.crazy.sql.core.proxy.executor.impl;

import com.crazy.sql.core.accessor.SqlBuilder;
import com.crazy.sql.core.cahce.dirty.DirtyManager;
import com.crazy.sql.core.cahce.key.CacheKey;
import com.crazy.sql.core.config.GlobalConfig;
import com.crazy.sql.core.proxy.executor.Executor;
import com.crazy.sql.core.manager.ConvertorManager;
import com.crazy.sql.core.jdbc.sql.SizeStatement;
import com.crazy.sql.core.jdbc.table.column.ColumnInfo;
import com.crazy.sql.core.jdbc.table.TableInfo;
import com.crazy.sql.core.jdbc.context.JdbcContext;
import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.jdbc.transaction.Transaction;
import com.crazy.sql.core.utils.jdbc.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Accessor.size()方法的执行器
 * @author Akiba no ichiichiyoha
 */
public class SizeExecutor implements Executor {
    private static final Logger logger= LoggerFactory.getLogger(SizeExecutor.class);
    @Override
    public Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object,Object> cache, String accessorHashCode, Object[] args) {
        /**
         * 检查sql构造器是否为空
         */
        SqlBuilder sqlBuilder =  args[0]==null?null:(SqlBuilder) args[0];
        /**
         * 获取事务
         */
        Transaction transaction=context.getTransaction();
        /**
         * 获取脏数据管理器
         */
        DirtyManager dirtyManager=context.getDirtyManager();
        try {
            /**
             * 执行size语句
             */
            Object result= openStatement(transaction.getConnection(),tableInfo, sqlBuilder,cache,accessorHashCode,dirtyManager);
            /**
             * 获取到的数据，一般是一个List对象
             */
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0L;
        }
    }
    private Object openStatement(Connection connection, TableInfo tableInfo, SqlBuilder sqlBuilder, Cache<Object,Object> cache, String accessorHashCode, DirtyManager dirtyManager) throws SQLException {
        /**
         * 获取SizeStatement这是一个size语句的抽象类
         */
        SizeStatement sizeStatement=getSizeStatement(tableInfo,sqlBuilder);
        /**
         * 整合资源
         */
        sizeStatement.integratingResource();
        /**
         * 获取sql
         */
        String sql=sizeStatement.getNativeSql();
        logger.debug("size() - sql:"+sql);
        /**
         * 创建缓存的key
         */
        CacheKey key=new CacheKey(tableInfo.getMappedClazz(),sql,accessorHashCode);
        Object count=null;
        if(GlobalConfig.getInstance().isCache()) {
            /**
             * 获取缓存中的数据
             */
            count = cache.get(key);
        }
        /**
         * 如果数据为空，那么从数据库中获取数据。
         */
        if(count==null){
            PreparedStatement preparedStatement=null;
            try{
                long start=System.currentTimeMillis();
                /**
                 * 预处理
                 */
                preparedStatement=connection.prepareStatement(sql);
                /**
                 * 设置参数
                 */
                JdbcUtils.setParam(sizeStatement,preparedStatement);
                ResultSet rs=preparedStatement.executeQuery();
                if(rs.next()){
                    count=rs.getLong(1);
                }
                rs.close();
                count=ConvertorManager.getConvertor(Long.class).convert(count);
                if(GlobalConfig.getInstance().isCache()){
                    /**
                     * 写入脏数据管理器
                     */
                    dirtyManager.get(cache).put(key,count);
                }
                logger.debug("Size completed,Elapsed time:"+(System.currentTimeMillis()-start)+"ms");
            }catch (Exception e){
                throw e;
            }finally {
                if(preparedStatement!=null)
                    preparedStatement.close();
            }
        }else
            logger.debug("Cache hit for key:"+key);
        return count;
    }
    private SizeStatement getSizeStatement(TableInfo tableInfo,SqlBuilder sqlBuilder){
        SizeStatement sizeStatement=new SizeStatement();
        sizeStatement.setTableName(tableInfo.getTableName());
        if(sqlBuilder !=null){
            sizeStatement.setWhereFragment(sqlBuilder.getWhereFragment());
            sizeStatement.setGroupByStatement(sqlBuilder.getGroupByFragment());
            sizeStatement.setHavingStatement(sqlBuilder.getHavingFragment());
            sizeStatement.setOrderByFragment(sqlBuilder.getOrderByFragment());
            sizeStatement.setLimitFragment(sqlBuilder.getLimitFragment());
            sizeStatement.setLastFragment(sqlBuilder.getLastFragment());
            if(sqlBuilder.getColumnList().size()==0){
                /**
                 * 判断是否含有主键，如果有，将主键列名放入待查询列中
                 */
                if(tableInfo.hasPk()) {
                    sizeStatement.setPrimaryName(tableInfo.getPkColumn().getColumnName());
                    sizeStatement.getSelectedColumns().add(tableInfo.getPkColumn().getColumnName());
                }
                /**
                 * 遍历列信息，将列名放入待查询列中
                 */
                for (ColumnInfo columnInfo:tableInfo.getColumnMappedMap().values()) {
                    sizeStatement.getSelectedColumns().add(columnInfo.getColumnName());
                }
            }else {
                sizeStatement.getSelectedColumns().addAll(sqlBuilder.getColumnList());
            }
        }else {
            /**
             * 判断是否含有主键，如果有，将主键列名放入待查询列中
             */
            if(tableInfo.hasPk()) {
                sizeStatement.setPrimaryName(tableInfo.getPkColumn().getColumnName());
                sizeStatement.getSelectedColumns().add(tableInfo.getPkColumn().getColumnName());
            }
            /**
             * 遍历列信息，将列名放入待查询列中
             */
            for (ColumnInfo columnInfo:tableInfo.getColumnMappedMap().values()) {
                sizeStatement.getSelectedColumns().add(columnInfo.getColumnName());
            }
        }
        return sizeStatement;

    }
}
