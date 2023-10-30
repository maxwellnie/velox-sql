package com.maxwellnie.velox.jpa.core.jdbc.table.primary.keyselector;

public interface KeySelector {
    Object selectGeneratorKey(Object... params);
}
