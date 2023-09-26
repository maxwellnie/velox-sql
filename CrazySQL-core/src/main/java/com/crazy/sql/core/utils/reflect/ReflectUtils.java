package com.crazy.sql.core.utils.reflect;

import com.crazy.sql.core.annotation.Table;
import com.crazy.sql.core.annotation.TableField;
import com.crazy.sql.core.annotation.TableId;
import com.crazy.sql.core.config.GlobalConfig;
import com.crazy.sql.core.enums.PrimaryMode;
import com.crazy.sql.core.proxy.executor.Executor;
import com.crazy.sql.core.java.type.impl.DefaultConvertor;
import com.crazy.sql.core.manager.ConvertorManager;
import com.crazy.sql.core.manager.KeyStrategyManager;
import com.crazy.sql.core.manager.MethodMappedManager;
import com.crazy.sql.core.jdbc.table.column.ColumnInfo;
import com.crazy.sql.core.jdbc.table.column.PrimaryInfo;
import com.crazy.sql.core.jdbc.table.TableInfo;
import com.crazy.sql.core.java.type.TypeConvertor;
import com.crazy.sql.core.utils.java.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 反射工具
 * @author Akiba no ichiichiyoha
 */
public class ReflectUtils {
    private static final Map<Class<?>, TableInfo> objMappedCache= Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * 获取clazz对应表信息
     * @param clazz
     * @return
     */
    public static TableInfo getTableInfo(Class<?> clazz){
        if(clazz==null)
            return null;
        else {
            TableInfo tableInfo;
            if((tableInfo=objMappedCache.get(clazz))==null){
                tableInfo=initTableInfo(clazz);
                objMappedCache.put(clazz,tableInfo);
            }
            return tableInfo;
        }
    }

    /**
     * 初始化clazz对应表信息
     * @param clazz
     * @return
     */
    private static TableInfo initTableInfo(Class<?> clazz){
        TableInfo tableInfo=new TableInfo();
        handleTable(clazz,tableInfo, GlobalConfig.getInstance().getTablePrefix(), GlobalConfig.getInstance().isStandTable());
        tableInfo.setMappedClazz(clazz);
        initFieldMapped(tableInfo,clazz, GlobalConfig.getInstance().isStandColumn());
        return tableInfo;
    }

    /**
     * !test unit
     * public static void main(String[] args) {
            @Table
            class User{
                @TableId(value = "user@id",primaryMode =PrimaryMode.OTHER,strategyKey = "other")
                long userId;
                String password;
            }
            System.out.println(getTableInfo(User.class));
        }
     **/
    public static String getClassName(Class<?> clazz){
        return clazz.getName();
    }

    /**
     * 将反射信息处理为表信息的一部分
     * @param clazz
     * @param tableInfo
     * @param prefix
     * @param openStandTableName
     */
    private static void handleTable(Class<?> clazz,TableInfo tableInfo,String prefix,boolean openStandTableName){
        String name = "";
        int fetchSize=0;
        if(clazz.isAnnotationPresent(Table.class)){
            Table table=clazz.getDeclaredAnnotation(Table.class);
            name=table.value();
            fetchSize=table.fetchSize();
        }
        if(StringUtils.isNullOrEmpty(name)){
            name=clazz.getSimpleName();
            if(openStandTableName)
                name=StringUtils.getStandName(name);
            if(!StringUtils.isNullOrEmpty(prefix))
                name=prefix+name;
        }
        tableInfo.setFetchSize(fetchSize);
        tableInfo.setTableName(name);
    }

    /**
     * 初始化字段映射
     * @param tableInfo
     * @param clazz
     * @param openStandColumn
     */
    private static void initFieldMapped(TableInfo tableInfo,Class<?> clazz,boolean openStandColumn){
        assert tableInfo!=null;
        Field[] fields=clazz.getDeclaredFields();
        for (Field f:fields) {
            if(f.isAnnotationPresent(TableId.class)){
                TableId tableId=f.getDeclaredAnnotation(TableId.class);
                String primaryName;
                PrimaryMode primaryMode=tableId.value();
                String strategy=tableId.strategyKey();
                primaryName=tableId.name();
                if(StringUtils.isNullOrEmpty(primaryName))
                    if(!openStandColumn)
                        primaryName=f.getName();
                    else
                        primaryName=StringUtils.getStandName(f.getName());
                strategy=getStrategy(primaryMode,strategy);
                PrimaryInfo primaryInfo=new PrimaryInfo(primaryName,f, ConvertorManager.getConvertor(f.getType()),primaryName,primaryMode,strategy);
                tableInfo.setPkColumn(primaryInfo);
                continue;
            }
            String columnName="";
            TypeConvertor convertor=null;
            if(f.isAnnotationPresent(TableField.class)){
                TableField tableField=f.getDeclaredAnnotation(TableField.class);
                if(tableField.isExclusion())
                    continue;
                try {
                    if(!tableField.convertor().equals(DefaultConvertor.class))
                        convertor=tableField.convertor().newInstance();
                    else
                        convertor=ConvertorManager.getConvertor(f.getType());
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                columnName=tableField.value();
            }
            if(StringUtils.isNullOrEmpty(columnName)){
                columnName=f.getName();
                if(openStandColumn)
                    columnName=StringUtils.getStandName(columnName);
                convertor=ConvertorManager.getConvertor(f.getType());
            }
            tableInfo.putColumnInfo(f.getName(),new ColumnInfo(columnName,f,convertor));
        }
    }

    /**
     * 获取主键策略
     * @param primaryMode
     * @param strategy
     * @return
     */
    private static String getStrategy(PrimaryMode primaryMode,String strategy){
        if(primaryMode.equals(PrimaryMode.NONE))
            return KeyStrategyManager.DEFAULT;
        else if(primaryMode.equals(PrimaryMode.JDBC_AUTO)){
            return KeyStrategyManager.JDBC_AUTO;
        }else
            return strategy;
    }

    /**
     * 获取列名
     * @param name
     * @param field
     * @param openStandColumn
     * @return
     */
    private static String getColumnName(String name,Field field,boolean openStandColumn){
        String result="";
        if(!StringUtils.isNullOrEmpty(name))
            result=name;
        else {
            result=field.getName();
            if(openStandColumn)
                result=StringUtils.getStandName(result);
        }
        return result;
    }

    /**
     * 获取被映射方法的处理器
     * @param method
     * @return
     */
    public static Executor getMethodMapped(Method method){
        assert method!=null;
        return MethodMappedManager.getRegisteredMapped(StringUtils.getMethodDeclaredName(method));
    }
}
