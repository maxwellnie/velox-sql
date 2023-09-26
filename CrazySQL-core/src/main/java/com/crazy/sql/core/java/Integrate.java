package com.crazy.sql.core.java;

import com.crazy.sql.core.jdbc.sql.SqlStatement;

/**
 * 资源初始化器，有些资源需要整合后才能进行操作。实现这个方法的类获取类提供的资源时需要先调用整合方法才可以继续获取。<br/>
 * 目前框架中{@link SqlStatement}的NativeSql和Values资源需要先整合才能继续访问资源
 * @author Akiba no ichiichiyoha
 */
public interface Integrate<T> {
    /**
     * 资源整合
     */
    void integratingResource();
}
