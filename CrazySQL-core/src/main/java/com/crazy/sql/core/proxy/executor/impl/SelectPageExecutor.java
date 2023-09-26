package com.crazy.sql.core.proxy.executor.impl;

import com.crazy.sql.core.accessor.SqlBuilder;
import com.crazy.sql.core.accessor.page.DataPage;
import com.crazy.sql.core.accessor.page.SimplePage;
import com.crazy.sql.core.exception.EntityObjectException;
import com.crazy.sql.core.proxy.executor.Executor;
import com.crazy.sql.core.jdbc.table.TableInfo;
import com.crazy.sql.core.jdbc.context.JdbcContext;
import com.crazy.sql.core.cahce.Cache;
import com.crazy.sql.core.utils.reflect.ReflectUtils;

import java.util.Collection;

/**
 * Accessor.selectPage()方法的执行器
 * @author Akiba no ichiichiyoha
 */
public class SelectPageExecutor implements Executor {
    @Override
    public Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object,Object> cache, String accessorHashCode, Object[] args) {
        DataPage dataPage = args[0]==null?null: (DataPage) args[0];
        SqlBuilder sqlBuilder = args[1]==null?null: (SqlBuilder) args[1];
        if(dataPage==null)
            throw new EntityObjectException("The page object "+ ReflectUtils.getClassName(DataPage.class) +" is null");
        SizeExecutor sizeExecutor=new SizeExecutor();
        long size= (long) sizeExecutor.execute(tableInfo,context,cache,accessorHashCode,args);
        if (size==0||(dataPage.getOffset()>size))
            return new QueryAllExecutor().execute(tableInfo,context,cache,accessorHashCode,args);
        return null;
    }
}
