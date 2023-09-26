package com.crazy.sql.core.manager;

import com.crazy.sql.core.proxy.executor.Executor;
import com.crazy.sql.core.proxy.executor.impl.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Accessor方法映射管理器，注册映射和获取映射<br/>
 * 注册时需要一个完整的方法名：example[class java.lang.Object, class java.lang.String]作为key，这个方法的处理器Executor作为value
 * @author Akiba no ichiichiyoha
 */
public class MethodMappedManager {
    private static final Map<String, Executor> methodMappedMap = Collections.synchronizedMap(new LinkedHashMap<>());
    /*
      初始化默认映射
     */
    static {
        methodMappedMap.put("insert[class java.lang.Object]",new InsertExecutor());
        methodMappedMap.put("update[class java.lang.Object, class com.crazy.sql.core.accessor.SqlBuilder]",new UpdateExecutor());
        methodMappedMap.put("delete[class com.crazy.sql.core.accessor.SqlBuilder]",new DeleteExecutor());
        methodMappedMap.put("queryAll[class com.crazy.sql.core.accessor.SqlBuilder]",new QueryAllExecutor());
        methodMappedMap.put("batchInsert[interface java.util.Collection]",new BatchInsertExecutor());
        methodMappedMap.put("batchDeleteByIds[interface java.util.Collection]",new BatchDeleteByIdsExecutor());
        methodMappedMap.put("size[class com.crazy.sql.core.accessor.SqlBuilder]",new SizeExecutor());
        methodMappedMap.put("selectPage[interface com.crazy.sql.core.accessor.page.DataPage, class com.crazy.sql.core.accessor.SqlBuilder]",new SelectPageExecutor());
    }

    /**
     * 获取被映射方法的处理器
     * @param name
     * @return
     */
    public static Executor getRegisteredMapped(String name){
        return methodMappedMap.get(name);
    }

    /**
     * 注册被映射方法的处理器
     * @param name
     * @param executor
     */
    public static void registeredMapped(String name,Executor executor){
        methodMappedMap.put(name,executor);
    }
}
