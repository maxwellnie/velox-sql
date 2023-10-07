package com.maxwellnie.vleox.jpa.core.utils.reflect;

import com.maxwellnie.vleox.jpa.core.annotation.Column;
import com.maxwellnie.vleox.jpa.core.annotation.Entity;
import com.maxwellnie.vleox.jpa.core.annotation.PrimaryKey;
import com.maxwellnie.vleox.jpa.core.annotation.RegisterMethod;
import com.maxwellnie.vleox.jpa.core.config.simple.CrazySqlConfig;
import com.maxwellnie.vleox.jpa.core.enums.PrimaryMode;
import com.maxwellnie.vleox.jpa.core.exception.RegisterMethodException;
import com.maxwellnie.vleox.jpa.core.java.type.TypeConvertor;
import com.maxwellnie.vleox.jpa.core.java.type.impl.DefaultConvertor;
import com.maxwellnie.vleox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.vleox.jpa.core.jdbc.table.column.ColumnInfo;
import com.maxwellnie.vleox.jpa.core.jdbc.table.column.PrimaryInfo;
import com.maxwellnie.vleox.jpa.core.manager.ConvertorManager;
import com.maxwellnie.vleox.jpa.core.manager.KeyStrategyManager;
import com.maxwellnie.vleox.jpa.core.manager.MethodMappedManager;
import com.maxwellnie.vleox.jpa.core.proxy.executor.Executor;
import com.maxwellnie.vleox.jpa.core.utils.java.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 反射工具
 *
 * @author Maxwell Nie
 */
public class ReflectUtils {
    private static final Map<Class<?>, TableInfo> objMappedCache = Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * 获取clazz对应表信息
     *
     * @param clazz
     * @return
     */
    public static TableInfo getTableInfo(Class<?> clazz) {
        if (clazz == null)
            return null;
        else {
            TableInfo tableInfo;
            if ((tableInfo = objMappedCache.get(clazz)) == null) {
                tableInfo = initTableInfo(clazz);
                objMappedCache.put(clazz, tableInfo);
            }
            return tableInfo;
        }
    }

    /**
     * 初始化clazz对应表信息
     *
     * @param clazz
     * @return
     */
    private static TableInfo initTableInfo(Class<?> clazz) {
        TableInfo tableInfo = new TableInfo();
        handleTable(clazz, tableInfo, CrazySqlConfig.getInstance().getTablePrefix(), CrazySqlConfig.getInstance().isStandTable());
        tableInfo.setMappedClazz(clazz);
        initFieldMapped(tableInfo, clazz, CrazySqlConfig.getInstance().isStandColumn());
        return tableInfo;
    }

    /**
     * <pre class="code">
     *  !test unit
     *   public static void main(String[] args) {
     *        &#064;Entity
     *        class User{
     *            &#064;PrimaryKey(value  = "user_id",primaryMode =PrimaryMode.OTHER,strategyKey = "other")
     *            long userId;
     *            String password;
     *        }
     *        System.out.println(getTableInfo(User.class));
     *    }
     * </pre>
     **/
    public static String getClassName(Class<?> clazz) {
        return clazz.getName();
    }

    /**
     * 将反射信息处理为表信息的一部分
     *
     * @param clazz              实体
     * @param tableInfo          表信息对象
     * @param prefix             前缀
     * @param openStandTableName 标准名是否开启
     */
    private static void handleTable(Class<?> clazz, TableInfo tableInfo, String prefix, boolean openStandTableName) {
        String name = "";
        int fetchSize = 0;
        if (clazz.isAnnotationPresent(Entity.class)) {
            Entity entity = clazz.getDeclaredAnnotation(Entity.class);
            name = entity.value();
            fetchSize = entity.fetchSize();
        }
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
    private static void initFieldMapped(TableInfo tableInfo, Class<?> clazz, boolean openStandColumn) {
        assert tableInfo != null;
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            if (f.isAnnotationPresent(PrimaryKey.class)) {
                PrimaryKey primaryKey = f.getDeclaredAnnotation(PrimaryKey.class);
                String primaryName;
                PrimaryMode primaryMode = primaryKey.value();
                String strategy = primaryKey.strategyKey();
                primaryName = primaryKey.name();
                if (StringUtils.isNullOrEmpty(primaryName))
                    if (!openStandColumn)
                        primaryName = f.getName();
                    else
                        primaryName = StringUtils.getStandName(f.getName());
                strategy = getStrategy(primaryMode, strategy);
                PrimaryInfo primaryInfo = new PrimaryInfo(primaryName, f, ConvertorManager.getConvertor(f.getType()), primaryName, primaryMode, strategy);
                tableInfo.setPkColumn(primaryInfo);
                continue;
            }
            String columnName = "";
            TypeConvertor<?> convertor = null;
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
                    throw new RuntimeException(e);
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
     * 获取主键策略
     *
     * @param primaryMode
     * @param strategy
     * @return
     */
    private static String getStrategy(PrimaryMode primaryMode, String strategy) {
        if (primaryMode.equals(PrimaryMode.NONE))
            return KeyStrategyManager.DEFAULT;
        else if (primaryMode.equals(PrimaryMode.JDBC_AUTO)) {
            return KeyStrategyManager.JDBC_AUTO;
        } else
            return strategy;
    }

    /**
     * 获取列名
     *
     * @param name
     * @param field
     * @param openStandColumn
     * @return
     */
    private static String getColumnName(String name, Field field, boolean openStandColumn) {
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

    /**
     * 获取被映射方法的处理器
     *
     * @param method
     * @return
     */
    public static Executor getMethodMapped(Method method) {
        assert method != null;
        return MethodMappedManager.getRegisteredMapped(StringUtils.getMethodDeclaredName(method));
    }

    public static void registerDaoImpl(Class<?> clazz) {
        assert clazz != null;
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(RegisterMethod.class)) {
                RegisterMethod registerMethod = method.getDeclaredAnnotation(RegisterMethod.class);
                if (registerMethod.value() != null) {
                    try {
                        MethodMappedManager.registeredMapped(StringUtils.getMethodDeclaredName(method), registerMethod.value().newInstance());
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RegisterMethodException("The executor " + registerMethod.value() + " of method " + method + "  cannot be instantiated.", e);
                    }
                }
            }
        }
    }

    public interface ExecutorRegister {
        void register(String methodName, Class<? extends Executor> executorClass) throws InstantiationException, IllegalAccessException;
    }

    public static class SimpleExecutorRegister implements ExecutorRegister {

        @Override
        public void register(String methodName, Class<? extends Executor> executorClass) throws InstantiationException, IllegalAccessException {
            MethodMappedManager.registeredMapped(methodName, executorClass.newInstance());
        }
    }
}
