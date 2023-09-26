package com.crazy.sql.core.jdbc.table.primary.keyselector;

public class NoKeySelector implements KeySelector{
    @Override
    public Object selectGeneratorKey(Object param) {
        return null;
    }
}
