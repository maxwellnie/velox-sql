package com.crazy.sql.core.java.type;

/**
 * @author Akiba no ichiichiyoha
 */
public interface TypeConvertor <T>{
    public T convert(Object original);
}
