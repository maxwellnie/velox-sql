package com.maxwellnie.velox.sql.core.natives.jdbc.mapping;

/**
 * 返回值映射
 */
public class ReturnTypeMapping {
    //返回值类型
    Class<?> type;
    //返回值类型映射
    TypeMapping typeMapping;
    boolean hasJoin;
    boolean isReturnManyObject;

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public TypeMapping getTypeMapping() {
        return typeMapping;
    }

    public void setTypeMapping(TypeMapping typeMapping) {
        this.typeMapping = typeMapping;
    }

    public boolean isHasJoin() {
        return hasJoin;
    }

    public void setHasJoin(boolean hasJoin) {
        this.hasJoin = hasJoin;
    }

    public boolean isReturnManyObject() {
        return isReturnManyObject;
    }

    public void setReturnManyObject(boolean returnManyObject) {
        isReturnManyObject = returnManyObject;
    }

    public ReturnTypeMapping() {
    }

    @Override
    public String toString() {
        return "ReturnTypeMapping{" +
                "type=" + type +
                ", typeMapping=" + typeMapping +
                ", hasJoin=" + hasJoin +
                ", isReturnManyObject=" + isReturnManyObject +
                '}';
    }
}
