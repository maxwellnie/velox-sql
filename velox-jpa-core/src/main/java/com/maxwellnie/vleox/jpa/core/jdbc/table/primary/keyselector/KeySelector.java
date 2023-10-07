package com.maxwellnie.vleox.jpa.core.jdbc.table.primary.keyselector;

public interface KeySelector {
    Object selectGeneratorKey(Object param);
}
