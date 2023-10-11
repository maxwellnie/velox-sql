package com.maxwellnie.velox.jpa.core.jdbc.table.primary;

import com.maxwellnie.velox.jpa.core.jdbc.table.primary.generator.Generator;
import com.maxwellnie.velox.jpa.core.jdbc.table.primary.keyselector.KeySelector;

/**
 * 主键策略
 */
public class PrimaryKeyStrategy {
    private final Generator keyGenerator;
    private final KeySelector keySelector;

    public PrimaryKeyStrategy(Generator keyGenerator, KeySelector keySelector) {
        this.keyGenerator = keyGenerator;
        this.keySelector = keySelector;
    }

    public Generator getKeyGenerator() {
        return keyGenerator;
    }

    public KeySelector getKeySelector() {
        return keySelector;
    }
}
