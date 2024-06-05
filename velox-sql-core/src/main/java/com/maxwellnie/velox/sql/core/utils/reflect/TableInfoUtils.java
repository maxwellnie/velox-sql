package com.maxwellnie.velox.sql.core.utils.reflect;

import com.maxwellnie.velox.sql.core.annotation.*;
import com.maxwellnie.velox.sql.core.config.Configuration;
import com.maxwellnie.velox.sql.core.manager.ConvertorManager;
import com.maxwellnie.velox.sql.core.meta.MetaData;
import com.maxwellnie.velox.sql.core.natives.concurrent.ThreadLock;
import com.maxwellnie.velox.sql.core.natives.exception.EntityObjectException;
import com.maxwellnie.velox.sql.core.natives.exception.TableException;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.TableInfo;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.column.PrimaryInfo;
import com.maxwellnie.velox.sql.core.natives.jdbc.table.join.JoinInfo;
import com.maxwellnie.velox.sql.core.natives.type.convertor.TypeConvertor;
import com.maxwellnie.velox.sql.core.natives.type.convertor.impl.DefaultConvertor;
import com.maxwellnie.velox.sql.core.utils.java.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 表信息工具类
 * @author Maxwell Nie
 */
public abstract class TableInfoUtils {
    private static final Logger logger = LoggerFactory.getLogger(TableInfoUtils.class);
    /**
     * 缓存表信息
     */
    private static final Map<Object, TableInfo> TABLE_INFO_CACHE = new LinkedHashMap<>();
    private final ReentrantReadWriteLock lock = ThreadLock.reentrantReadWriteLock("TABLE_INFO_CACHE_READ_WRITE_LOCK");
    /**
     * 获取clazz对应表信息
     *
     * @param o
     * @return 表信息
     */
    public static TableInfo getTableInfo(Object o) {
        return TABLE_INFO_CACHE.get(o);
    }
    /**
     * 获取clazz对应表信息
     *
     * @param clazz
     * @return 表信息
     */
    public TableInfo getCachedTableInfo(Class<?> clazz) {
        try {
            lock.readLock().lock();
            return TABLE_INFO_CACHE.get(clazz);
        }finally {
            lock.readLock().unlock();
        }

    }
    /**
     * 获取clazz对应表信息
     *
     * @param configuration
     * @param clazz
     * @return 表信息
     */
    public TableInfo getTableInfo(Class<?> clazz, Configuration configuration) {
        if (clazz == null)
            return null;
        else {
            TableInfo tableInfo;
            if ((tableInfo = TABLE_INFO_CACHE.get(clazz)) == null) {
                tableInfo = initTableInfo(clazz, configuration);
                TABLE_INFO_CACHE.put(clazz, tableInfo);
            }
            return tableInfo;
        }
    }
    public static Set<Object> getAllRegisteredClass(){
        return TABLE_INFO_CACHE.keySet();
    }
    /**
     * 初始化clazz对应表信息
     *
     * @param configuration
     * @param clazz
     * @return 表信息
     */
    private synchronized TableInfo initTableInfo(Class<?> clazz, Configuration configuration) {
        if(!Serializable.class.isAssignableFrom(clazz))
            throw new EntityObjectException("The entity '" + clazz.getName() + "' is not serializable,Please implements Serializable interface.");
        TableInfo tableInfo = new TableInfo();
        tableInfo.setMappedClazz(clazz);
        handleTable(clazz, tableInfo, configuration.getTablePrefix(), configuration);
        initFieldMapped(tableInfo, clazz, configuration);
        return tableInfo;
    }

    /**
     * 将反射信息处理为表信息的一部分
     *
     * @param clazz      实体
     * @param tableInfo  表信息对象
     * @param prefix     前缀
     * @param configuration 配置
     */
    private void handleTable(Class<?> clazz, TableInfo tableInfo, String prefix, Configuration configuration) {
        String name;
        int fetchSize;
        if (clazz.isAnnotationPresent(Entity.class)) {
            if (configuration.isCache() && !ReflectionUtils.hasInterface(clazz, Serializable.class)) {
                throw new EntityObjectException("The entity '" + clazz.getName() + "' is not serializable,Please implements Serializable interface.");
            }
            Entity entity = clazz.getDeclaredAnnotation(Entity.class);
            name = entity.value();
            fetchSize = entity.fetchSize();
        } else
            throw new TableException("The class '" + clazz.getName() + "' is not entity,Please check your entity class.It must use @Entity when it's entity.");
        if (StringUtils.isNullOrEmpty(name)) {
            name = clazz.getSimpleName();
            if (configuration.isStandTable())
                name = StringUtils.getStandName(name);
            if (!StringUtils.isNullOrEmpty(prefix))
                name = prefix + name;
        }
        if(clazz.isAnnotationPresent(JoinTable.class)){
            handleTableTypeJoin(clazz, tableInfo);
        }
        tableInfo.setFetchSize(fetchSize);
        tableInfo.setTableName(name);
    }

    private void handleTableTypeJoin(Class<?> clazz, TableInfo tableInfo) {
        JoinTable[] joins = clazz.getDeclaredAnnotationsByType(JoinTable.class);
        for (JoinTable join : joins){
            JoinInfo joinInfo = new JoinInfo();
            assert StringUtils.isNotNullOrEmpty(join.masterTableField()) : "The masterTableField must not be null.";
            assert StringUtils.isNotNullOrEmpty(join.slaveTableColumn()) : "The slaveTableField must not be null.";
            joinInfo.setMasterTableField(join.masterTableField());
            joinInfo.setMasterTable(clazz);
            joinInfo.setAliasSlaveTable(join.slaveTableAlias());
            joinInfo.setSlaveTableName(join.slaveTableName());
            joinInfo.setSlaveTableColumn(join.slaveTableColumn());
            joinInfo.setJoinType(join.joinType());
            joinInfo.setManyToMany(join.isManyToMany());
            joinInfo.setNotNested(true);
            TableInfo slaveTableInfo = new TableInfo();
            slaveTableInfo.setTableName(join.slaveTableName());
            slaveTableInfo.setMappedClazz(clazz);
            slaveTableInfo.setFetchSize(tableInfo.getFetchSize());
            TABLE_INFO_CACHE.put(clazz.getName() + " - " + join.slaveTableName(), slaveTableInfo);
            tableInfo.getJoinInfos().add(joinInfo);
        }
    }

    /**
     * 初始化字段映射
     *
     * @param tableInfo  表信息
     * @param clazz      实体
     * @param configuration 配置
     */
    private void initFieldMapped(TableInfo tableInfo, Class<?> clazz, Configuration configuration) {
        assert tableInfo != null;
        List<Field> fields = ReflectionUtils.getAllFields(clazz);
        for (Field f : fields) {
            if (f.isAnnotationPresent(Join.class)){
                handleJoin(tableInfo, f, clazz);
            }else if(f.isAnnotationPresent(SlaveField.class)){
                SlaveField slaveField = f.getDeclaredAnnotation(SlaveField.class);
                for (JoinInfo joinInfo: tableInfo.getJoinInfos()) {
                    if(joinInfo.getSlaveTableName().equals(slaveField.slaveTableName())){
                        TableInfo slaveTableInfo =  TABLE_INFO_CACHE.get(clazz.getName() + " - " +joinInfo.getSlaveTableName());
                        if(slaveTableInfo == null){
                            throw new EntityObjectException("The slaveTableName '" + slaveField.slaveTableName() + "' is not found.Please make sure that the slave table is created when the master table is initialized,and try again.");
                        }
                        handleColumn(slaveTableInfo, configuration, f);
                    }
                }
            }else {
                handleColumn(tableInfo, configuration, f);
            }
        }
        MetaData metaData = MetaData.ofEmpty();
        for (Field f : fields) {
            metaData.addProperty(f.getName(), f);
        }
        tableInfo.getOtherInfo().addProperty("fields", metaData);
    }
    /**
     * 处理字段
     *
     * @param tableInfo  表信息
     * @param configuration 配置
     * @param f          字段
     */
    private void handleColumn(TableInfo tableInfo, Configuration configuration, Field f) {
        String columnName = "";
        TypeConvertor<?> convertor = null;
        if (f.isAnnotationPresent(PrimaryKey.class)) {
            PrimaryKey primaryKey = f.getDeclaredAnnotation(PrimaryKey.class);
            String strategy = primaryKey.strategyKey();
            columnName = primaryKey.name();
            if (StringUtils.isNullOrEmpty(columnName))
                if (!configuration.isStandColumn())
                    columnName = f.getName();
                else
                    columnName = StringUtils.getStandName(f.getName());
            try {
                if (!primaryKey.convertor().equals(DefaultConvertor.class))
                    convertor = primaryKey.convertor().getConstructor().newInstance();
                else
                    convertor = ConvertorManager.getConvertor(f.getType());
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                logger.error(e.getMessage() + "\t\n" + e.getCause());
            }
            PrimaryInfo primaryInfo = new PrimaryInfo(columnName, ReflectionUtils.getMetaField(tableInfo.getMappedClazz(), f), convertor, strategy);
            tableInfo.setPkColumn(primaryInfo);
            return;
        }
        if (f.isAnnotationPresent(Column.class)) {
            Column column = f.getDeclaredAnnotation(Column.class);
            if (column.isExclusion())
                return;
            try {
                if (!column.convertor().equals(DefaultConvertor.class))
                    convertor = column.convertor().newInstance();
                else
                    convertor = ConvertorManager.getConvertor(f.getType());
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error(e.getMessage() + "\t\n" + e.getCause());
            }
            columnName = column.value();
        }
        if (StringUtils.isNullOrEmpty(columnName)) {
            columnName = f.getName();
            if (configuration.isStandColumn())
                columnName = StringUtils.getStandName(columnName);
            convertor = ConvertorManager.getConvertor(f.getType());
        }
        ColumnInfo columnInfo = new ColumnInfo(columnName, ReflectionUtils.getMetaField(tableInfo.getMappedClazz(), f), convertor);
        tableInfo.putColumnInfo(f.getName(), columnInfo);
    }
    /**
     * 处理join
     * @param tableInfo
     * @param f
     */
    private void handleJoin(TableInfo tableInfo, Field f, Class<?> clazz) {
        Join join = f.getDeclaredAnnotation(Join.class);
        JoinInfo joinInfo = new JoinInfo();
        assert StringUtils.isNotNullOrEmpty(join.masterTableField()) : "The masterTableField must not be null.";
        assert StringUtils.isNotNullOrEmpty(join.slaveTableField()) : "The slaveTableField must not be null.";
        joinInfo.setMasterTableField(join.masterTableField());
        joinInfo.setMasterTable(clazz);
        joinInfo.setAliasSlaveTable(join.slaveTableAlias());
        joinInfo.setSlaveTable(join.slaveTable());
        joinInfo.setSlaveTableField(join.slaveTableField());
        joinInfo.setJoinType(join.joinType());
        joinInfo.setManyToMany(join.isManyToMany());
        joinInfo.setField(ReflectionUtils.getMetaField(clazz, f));
        joinInfo.setColumnTypeClass(f.getType());
        tableInfo.getJoinInfos().add(joinInfo);
    }

    /**
     * 获取列名
     *
     * @param name
     * @param field
     * @param openStandColumn
     * @return
     */
    private String getColumnName(String name, Field field, boolean openStandColumn) {
        String result = "";
        if (!StringUtils.isNullOrEmpty(name))
            result = name;
        else {
            result = field.getName();
            if (openStandColumn)
                result = StringUtils.getStandName(result);
        }
        return result;
    }
}
