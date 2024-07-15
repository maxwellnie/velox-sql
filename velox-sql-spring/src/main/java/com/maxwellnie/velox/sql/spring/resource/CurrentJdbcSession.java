package com.maxwellnie.velox.sql.spring.resource;

import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSession;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.JdbcSessionFactory;
import com.maxwellnie.velox.sql.core.natives.jdbc.session.impl.JdbcSessionProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * 当前线程所持有的JdbcSession
 *
 * @author Maxwell Nie
 */
public class CurrentJdbcSession {
    private static final Logger logger = LoggerFactory.getLogger(CurrentJdbcSession.class);
    private static final ThreadLocal<JdbcSessionProxy> jdbcSessionThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> enabledProxySpringTransaction = new ThreadLocal<>();

    /**
     * 提交事务
     */
    public static void commit() {
        if (isOpenProxyTransaction())
            Optional.ofNullable(getJdbcSession()).ifPresent((o) -> o.getTarget().commit());
    }

    /**
     * 释放JdbcSession
     */
    public static void close() {
        if (isOpenProxyTransaction()) {
            JdbcSessionProxy jdbcSession = getJdbcSession();
            JdbcSessionFactory jdbcSessionFactory = jdbcSession.getJdbcSessionFactory();
            if (jdbcSession != null && jdbcSession.getTarget() != null && jdbcSessionFactory != null)
                JdbcSessionUtils.releaseJdbcSession(jdbcSession.getTarget(), jdbcSessionFactory);
            else
                logger.error("jdbcSession or jdbcSessionFactory is null");
            closeProxyTransaction();
        }
    }

    /**
     * 关闭事务
     */
    public static void closeProxyTransaction() {
        if (isOpenProxyTransaction()) {
            enabledProxySpringTransaction.remove();
            jdbcSessionThreadLocal.remove();
        }
    }

    /**
     * 回滚事务
     */
    public static void rollback() {
        if (isOpenProxyTransaction())
            Optional.ofNullable(getJdbcSession()).ifPresent((o) -> o.getTarget().rollback());
    }

    /**
     * 开启事务
     */
    public static void openProxyTransaction() {
        enabledProxySpringTransaction.set(Boolean.TRUE);
    }

    /**
     * 是否开启事务
     *
     * @return boolean
     */
    public static boolean isOpenProxyTransaction() {
        return enabledProxySpringTransaction.get() != null && enabledProxySpringTransaction.get();
    }

    /**
     * 代理事务
     *
     * @param jdbcSession
     * @param jdbcSessionFactory
     * @return JdbcSessionProxy
     */
    public static JdbcSessionProxy proxyCurrentSpringTransaction(JdbcSession jdbcSession, JdbcSessionFactory jdbcSessionFactory) {
        if (isOpenProxyTransaction()) {
            JdbcSessionProxy proxyJdbcSession = new JdbcSessionProxy(jdbcSession, jdbcSessionFactory);
            jdbcSessionThreadLocal.set(proxyJdbcSession);
            return proxyJdbcSession;
        }
        return null;
    }

    /**
     * 获取当前线程的JdbcSession
     *
     * @return JdbcSessionProxy
     */
    public static JdbcSessionProxy getJdbcSession() {
        return jdbcSessionThreadLocal.get();
    }
}
