package com.maxwellnie.vleox.jpa.core.proxy.executor.impl;

import com.maxwellnie.vleox.jpa.core.cahce.Cache;
import com.maxwellnie.vleox.jpa.core.dao.support.SqlBuilder;
import com.maxwellnie.vleox.jpa.core.dao.support.page.DataPage;
import com.maxwellnie.vleox.jpa.core.exception.EntityObjectException;
import com.maxwellnie.vleox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.vleox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.vleox.jpa.core.proxy.executor.Executor;
import com.maxwellnie.vleox.jpa.core.utils.reflect.ReflectUtils;

/**
 * DaoImpl.selectPage()方法的执行器
 *
 * @author Maxwell Nie
 */
public class SelectPageExecutor implements Executor {
    @Override
    public Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object, Object> cache, String daoImplHashCode, Object[] args) {
        DataPage dataPage = args[0] == null ? null : (DataPage) args[0];
        SqlBuilder sqlBuilder = args[1] == null ? null : (SqlBuilder) args[1];
        if (dataPage == null)
            throw new EntityObjectException("The page object " + ReflectUtils.getClassName(DataPage.class) + " is null");
        SizeExecutor sizeExecutor = new SizeExecutor();
        long size = (long) sizeExecutor.execute(tableInfo, context, cache, daoImplHashCode, args);
        if (size == 0 || (dataPage.getOffset() > size))
            return new QueryAllExecutor().execute(tableInfo, context, cache, daoImplHashCode, args);
        return null;
    }
}
