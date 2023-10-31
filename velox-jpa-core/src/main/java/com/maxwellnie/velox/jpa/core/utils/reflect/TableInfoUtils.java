package com.maxwellnie.velox.jpa.core.utils.reflect;

import com.maxwellnie.velox.jpa.core.annotation.Column;
import com.maxwellnie.velox.jpa.core.annotation.Entity;
import com.maxwellnie.velox.jpa.core.annotation.PrimaryKey;
import com.maxwellnie.velox.jpa.core.config.BaseConfig;
import com.maxwellnie.velox.jpa.core.java.type.TypeConvertor;
import com.maxwellnie.velox.jpa.core.java.type.impl.DefaultConvertor;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.column.PrimaryInfo;
import com.maxwellnie.velox.jpa.core.manager.ConvertorManager;
import com.maxwellnie.velox.jpa.core.utils.java.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 反射工具
 *
 * @author Maxwell Nie
 */
public abstract class TableInfoUtils {
    private static final Logger logger = LoggerFactory.getLogger(TableInfoUtils.class);
    private static final Map<Class<?>, TableInfo> TABLE_INFO_CACHE = Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * 获取clazz对应表信息
     *
     * @param baseConfig
     * @param clazz
     * @return 表信息
     */
    public TableInfo getTableInfo(Class<?> clazz, BaseConfig baseConfig) {
        if (clazz == null)
            return null;
        else {
            TableInfo tableInfo;
            if ((tableInfo = TABLE_INFO_CACHE.get(clazz)) == null) {
                tableInfo = initTableInfo(clazz, baseConfig);
                TABLE_INFO_CACHE.put(clazz, tableInfo);
            }
            return tableInfo;
        }
    }

    /**
     * 初始化clazz对应表信息
     *
     * @param baseConfig
     * @param clazz
     * @return 表信息
     */
    private TableInfo initTableInfo(Class<?> clazz, BaseConfig baseConfig) {
        TableInfo tableInfo = new TableInfo();
        handleTable(clazz, tableInfo, baseConfig.getTablePrefix(), baseConfig.isStandTable());
        tableInfo.setMappedClazz(clazz);
        initFieldMapped(tableInfo, clazz, baseConfig.isStandColumn());
        return tableInfo;
    }

    /**
     * 将反射信息处理为表信息的一部分
     *
     * @param clazz              实体
     * @param tableInfo          表信息对象
     * @param prefix             前缀
     * @param openStandTableName 标准名是否开启
     */
    private void handleTable(Class<?> clazz, TableInfo tableInfo, String prefix, boolean openStandTableName) {
        String name;
        int fetchSize;
        if (clazz.isAnnotationPresent(Entity.class)) {
            Entity entity = clazz.getDeclaredAnnotation(Entity.class);
            name = entity.value();
            fetchSize = entity.fetchSize();
        } else
            return;
        if (StringUtils.isNullOrEmpty(name)) {
            name = clazz.getSimpleName();
            if (openStandTableName)
                name = StringUtils.getStandName(name);
            if (!StringUtils.isNullOrEmpty(prefix))
                name = prefix + name;
        }
        tableInfo.setFetchSize(fetchSize);
        tableInfo.setTableName(name);
    }

    /**
     * 初始化字段映射
     *
     * @param tableInfo       表信息
     * @param clazz           实体
     * @param openStandColumn 标准名是否开启
     */
    private void initFieldMapped(TableInfo tableInfo, Class<?> clazz, boolean openStandColumn) {
        assert tableInfo != null;
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            String columnName = "";
            TypeConvertor<?> convertor = null;
            if (f.isAnnotationPresent(PrimaryKey.class)) {
                PrimaryKey primaryKey = f.getDeclaredAnnotation(PrimaryKey.class);
                String strategy = primaryKey.strategyKey();
                columnName = primaryKey.name();
                if (StringUtils.isNullOrEmpty(columnName))
                    if (!openStandColumn)
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
                PrimaryInfo primaryInfo = new PrimaryInfo(columnName, f, convertor, columnName, strategy);
                tableInfo.setPkColumn(primaryInfo);
                continue;
            }
            if (f.isAnnotationPresent(Column.class)) {
                Column column = f.getDeclaredAnnotation(Column.class);
                if (column.isExclusion())
                    continue;
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
                if (openStandColumn)
                    columnName = StringUtils.getStandName(columnName);
                convertor = ConvertorManager.getConvertor(f.getType());
            }
            tableInfo.putColumnInfo(f.getName(), new ColumnInfo(columnName, f, convertor));
        }
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
