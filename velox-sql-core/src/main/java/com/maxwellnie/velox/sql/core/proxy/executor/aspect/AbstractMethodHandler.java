package com.maxwellnie.velox.sql.core.proxy.executor.aspect;

/**
 * @author Maxwell Nie
 */
public abstract class AbstractMethodHandler implements MethodHandler {
    /**
     * 权重
     */
    private long index;
    /**
     * 切面
     */
    private MethodAspect[] aspects;
    private TargetMethodSignature signature;

    public AbstractMethodHandler(long index, MethodAspect[] aspects, TargetMethodSignature signature) {
        this.index = index;
        this.aspects = aspects;
        this.signature = signature;
    }

    /**
     * 重写compareTo方法，用于比较MethodHandler对象之间的顺序
     *
     * @param o 要比较的MethodHandler对象
     * @return 返回值为0表示当前对象和传入对象的index值相同，返回值为-1表示当前对象的index值大于传入对象的index值，返回值为1表示当前对象的index值小于传入对象的index值
     */
    @Override
    public int compareTo(MethodHandler o) {
        long currentWeight = this.index - o.getIndex();
        return currentWeight == 0 ? -1 : currentWeight < 0 ? -1 : 1;
    }

    @Override
    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    @Override
    public MethodAspect[] getMethodAspects() {
        return aspects;
    }

    @Override
    public TargetMethodSignature getTargetMethodSignature() {
        return this.signature;
    }

    public void setAspects(MethodAspect[] aspects) {
        this.aspects = aspects;
    }
}
