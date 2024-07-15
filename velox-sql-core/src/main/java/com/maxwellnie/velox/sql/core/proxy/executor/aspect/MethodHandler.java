package com.maxwellnie.velox.sql.core.proxy.executor.aspect;

import com.maxwellnie.velox.sql.core.utils.java.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * 处理方法的接口。
 *
 * @author Maxwell Nie
 */
public interface MethodHandler extends Comparable<MethodHandler> {
    /**
     * 序号越高，被代理的优先级越高，意味着这个方法处理器最先执行，处于调用链的最顶层。最高不建议超过9999L，因为框架为spring支持事务提供JdbcSession应该是最优先的。
     */
    long SPRING_SUPPORT_INDEX = 9999L;
    /**
     * 日志的序号。
     */
    long LOGGER_SUPPORT_INDEX = 9997L;

    /**
     * 对切面方法处理。
     *
     * @param simpleInvocation
     * @return
     */
    Object handle(SimpleInvocation simpleInvocation);

    /**
     * 序号越高，被代理的优先级越高。最高不建议超过9999L，因为框架为spring支持事务提供JdbcSession应该是最优先的。
     */
    long getIndex();

    /**
     * 处理方法的切面
     */
    MethodAspect[] getMethodAspects();

    /**
     * 目标方法签名
     */
    TargetMethodSignature getTargetMethodSignature();

    /**
     * 处理方法的切面
     */
    class MethodAspect {
        /**
         * 匹配所有方法
         */
        public static final MethodAspect ANY = new MethodAspect("*", new Class[0]);
        /**
         * 匹配所有方法的切面
         */
        public static final MethodAspect[] ANY_FLAG = new MethodAspect[]{new MethodAspect("*", new Class[0])};
        /**
         * 切面名称
         */
        String name;
        /**
         * 切面参数类型
         */
        Class<?>[] args;

        public MethodAspect(String name, Class<?>[] args) {
            this.name = name;
            this.args = args;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Class<?>[] getArgs() {
            return args;
        }

        public void setArgs(Class<?>[] args) {
            this.args = args;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null) return false;
            if (getClass() != o.getClass()) {
                if (o.getClass().equals(Method.class)) return isMatch((Method) o);
                else return false;
            }
            MethodAspect that = (MethodAspect) o;
            return Objects.equals(name, that.name) && Arrays.equals(args, that.args);
        }

        public boolean isMatch(Method method) {
            return Objects.equals(name, method.getName()) && Arrays.equals(args, method.getParameterTypes());
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(name);
            result = 31 * result + Arrays.hashCode(args);
            return result;
        }
    }

    class TargetMethodSignature {
        public static final TargetMethodSignature ANY = new TargetMethodSignature("*", new Class[0]);
        /**
         * 方法名
         */
        String name;
        /**
         * 参数类型
         */
        Class<?>[] args;

        public TargetMethodSignature(String name, Class<?>[] args) {
            this.name = name;
            this.args = args;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Class<?>[] getArgs() {
            return args;
        }

        public String key() {
            return StringUtils.getMethodDeclaredName(name, args);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TargetMethodSignature that = (TargetMethodSignature) o;
            return Objects.equals(name, that.name) && Arrays.equals(args, that.args);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(name);
            result = 31 * result + Arrays.hashCode(args);
            return result;
        }
    }
}
