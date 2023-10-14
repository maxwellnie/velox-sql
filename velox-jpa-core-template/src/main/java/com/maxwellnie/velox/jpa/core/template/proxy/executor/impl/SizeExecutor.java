package com.maxwellnie.velox.jpa.core.template.proxy.executor.impl;

import com.maxwellnie.velox.jpa.core.config.simple.VeloxJpaConfig;
import com.maxwellnie.velox.jpa.core.template.dao.SqlBuilder;
import com.maxwellnie.velox.jpa.core.template.sql.SizeStatement;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.jpa.core.jdbc.transaction.Transaction;
import com.maxwellnie.velox.jpa.core.manager.ConvertorManager;
import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.cahce.dirty.CacheDirtyManager;
import com.maxwellnie.velox.jpa.core.cahce.key.CacheKey;
import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.velox.jpa.core.template.proxy.executor.BaseQueryExecutor;
import com.maxwellnie.velox.jpa.core.template.utils.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DaoImpl.size()方法的执行器
 *
 * @author Maxwell Nie
 */
public class SizeExecutor extends BaseQueryExecutor {
    private static final Logger logger = LoggerFactory.getLogger(SizeExecutor.class);

    @Override
    public Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object, Object> cache, String daoImplHashCode, Object[] args) {
        /**
         * 检查sql构造器是否为空
         */
        SqlBuilder sqlBuilder = args[0] == null ? null : (SqlBuilder) args[0];
        /**
         * 获取事务
         */
        Transaction transaction = context.getTransaction();
        /**
         * 获取脏数据管理器
         */
        CacheDirtyManager cacheDirtyManager = context.getDirtyManager();
        try {
            /**
             * 执行size语句
             */
            Map<String, Object> resultMap = openStatement(transaction.getConnection(), tableInfo, sqlBuilder, cache, daoImplHashCode, cacheDirtyManager);
            /**
             * 获取到的数据，一般是一个List对象
             */
            Object result = resultMap.get("result");
            CacheKey cacheKey = (CacheKey) resultMap.get("cacheKey");
            if((boolean)resultMap.get("needFlush"))
                flushCache(result, cacheKey, cache, cacheDirtyManager, !context.getAutoCommit());
            flushCache(result, cacheKey, cache, cacheDirtyManager, !context.getAutoCommit());
            return result;
        } catch (SQLException e){
            logger.error("The sql error :"+e.getMessage()+"\t\n"+e.getCause());
            return 0L;
        }
    }

    @Override
    protected Map<String, Object> openStatement(Connection connection, TableInfo tableInfo, SqlBuilder sqlBuilder, Cache<Object, Object> cache, String daoImplHashCode, CacheDirtyManager cacheDirtyManager) throws SQLException {
        /**
         * 获取SizeStatement这是一个size语句的抽象类
         */
        SizeStatement sizeStatement = getSizeStatement(tableInfo, sqlBuilder);
        /**
         * 整合资源
         */
        sizeStatement.integratingResource();
        /**
         * 获取sql
         */
        String sql = sizeStatement.getNativeSql();
        logger.debug("size() - sql:" + sql);
        Map<String, Object> resultMap = new LinkedHashMap<>();
        /**
         * 创建缓存的key
         */
        CacheKey key = new CacheKey(tableInfo.getMappedClazz(), sql, daoImplHashCode);
        key.addValueCollection(sizeStatement.getValues());
        resultMap.put("cacheKey", key);
        Object count = null;
        if (VeloxJpaConfig.getInstance().isCache()) {
            /**
             * 获取缓存中的数据
             */
            count = cache.get(key);
        }
        resultMap.put("needFlush",count==null);
        /**
         * 如果数据为空，那么从数据库中获取数据。
         */
        if (count == null) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                long start = System.currentTimeMillis();
                /**
                 * 设置参数
                 */
                JdbcUtils.setParam(sizeStatement, preparedStatement);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    count = rs.getLong(1);
                }
                rs.close();
                count = ConvertorManager.getConvertor(Long.class).convert(count);
                logger.debug("Size completed,Elapsed time:" + (System.currentTimeMillis() - start) + "ms");
            }
        } else
            logger.debug("Cache hit for key:" + key);
        resultMap.put("result", count);
        return resultMap;
    }

    private SizeStatement getSizeStatement(TableInfo tableInfo, SqlBuilder sqlBuilder) {
        SizeStatement sizeStatement = new SizeStatement();
        sizeStatement.setTableName(tableInfo.getTableName());
        if (sqlBuilder != null) {
            sizeStatement.setWhereFragment(sqlBuilder.getWhereFragment());
            sizeStatement.setGroupByStatement(sqlBuilder.getGroupByFragment());
            sizeStatement.setHavingStatement(sqlBuilder.getHavingFragment());
            sizeStatement.setOrderByFragment(sqlBuilder.getOrderByFragment());
            sizeStatement.setLimitFragment(sqlBuilder.getLimitFragment());
            sizeStatement.setLastFragment(sqlBuilder.getLastFragment());
            if (sqlBuilder.getColumnList().size() == 0) {
                /**
                 * 判断是否含有主键，如果有，将主键列名放入待查询列中
                 */
                if (tableInfo.hasPk()) {
                    sizeStatement.setPrimaryName(tableInfo.getPkColumn().getColumnName());
                    sizeStatement.getSelectedColumns().add(tableInfo.getPkColumn().getColumnName());
                }
                /**
                 * 遍历列信息，将列名放入待查询列中
                 */
                for (ColumnInfo columnInfo : tableInfo.getColumnMappedMap().values()) {
                    sizeStatement.getSelectedColumns().add(columnInfo.getColumnName());
                }
            } else {
                sizeStatement.getSelectedColumns().addAll(sqlBuilder.getColumnList());
            }
        } else {
            /**
             * 判断是否含有主键，如果有，将主键列名放入待查询列中
             */
            if (tableInfo.hasPk()) {
                sizeStatement.setPrimaryName(tableInfo.getPkColumn().getColumnName());
                sizeStatement.getSelectedColumns().add(tableInfo.getPkColumn().getColumnName());
            }
            /**
             * 遍历列信息，将列名放入待查询列中
             */
            for (ColumnInfo columnInfo : tableInfo.getColumnMappedMap().values()) {
                sizeStatement.getSelectedColumns().add(columnInfo.getColumnName());
            }
        }
        return sizeStatement;

    }
}
