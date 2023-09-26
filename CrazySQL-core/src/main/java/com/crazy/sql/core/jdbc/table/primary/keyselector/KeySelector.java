package com.crazy.sql.core.jdbc.table.primary.keyselector;

public interface KeySelector {
    Object selectGeneratorKey(Object param);
}
