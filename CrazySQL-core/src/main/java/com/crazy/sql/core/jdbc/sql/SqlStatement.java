package com.crazy.sql.core.jdbc.sql;

import com.crazy.sql.core.java.Integrate;
import com.crazy.sql.core.exception.NotIntegratingResourceException;

import java.util.LinkedList;
import java.util.List;

/**
 * 这个类是一个完整的Sql语句的抽象。<br/>
 * 请注意，SqlStatement继承了初始化器，NativeSql和Values需要整合sql后才可以访问和操作。
 * @author Akiba no ichiichiyoha
 */
public abstract class SqlStatement implements SqlFragment, Integrate<SqlStatement> {
    /**
     * 被操作的表名
     */
    private String tableName;
    /**
     * Where片段
     */
    private WhereFragment whereFragment;
    /**
     * sql语句中最后一个片段
     */
    private LastFragment lastFragment;
    /**
     * 这是一个需要被整合的资源
     * PreparedStatement设置参数时需要的参数列表。
     */
    private List<Object> values= new LinkedList<>();
    /**
     * 整合好的sql语句
     */
    private String nativeSql;
    /**
     * 资源锁，只允许设置一次参数的初始值
     */
    private volatile boolean lock=false;

    /**
     * 被整合的资源只能获取一次
     * @return
     */
    @Override
    public String getNativeSql() {
        if(nativeSql!=null){
            String ns=nativeSql;
            nativeSql=null;
            return ns;
        }else
            throw new NotIntegratingResourceException("NativeSql is not integrated.");
    }

    public void setNativeSql(String nativeSql) {
        this.nativeSql = nativeSql;
    }

    /**
     * 危险！！！
     * 尽量不要使用这个方法，这个方法可能会对未来的sql整合造成不可估计的影响，如果一定要使用，请先整合sql。
     * @return
     */
    public List<Object> getValues() {
        return values;
    }

    /**
     * 设置sql的基本初始参数,只能设置一次
     * @param params
     */
    public boolean setParams(List<Object> params){
        if(!lock){
            lock=true;
            this.values=params;
            return lock;
        }
        return !lock;
    }

    public boolean isLock() {
        return lock;
    }

    /**
     * 危险！！！
     * 这个方法会在未整合Sql前修改携带的参数，请勿在整合前使用这个方法
     * @param values
     */
    void setValues(List<Object> values) {
        this.values = values;
    }
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public WhereFragment getWhereFragment() {
        return whereFragment;
    }

    public void setWhereFragment(WhereFragment whereFragment) {
        this.whereFragment = whereFragment;
    }

    public LastFragment getLastFragment() {
        return lastFragment;
    }

    public void setLastFragment(LastFragment lastFragment) {
        this.lastFragment = lastFragment;
    }
}
