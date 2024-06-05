package com.maxwellnie.velox.sql.core.natives.enums;
/**
 * 某些sql关键字
 */
public enum KeyWords {
    LIMIT("LIMIT"), GROUP_BY("GROUP BY"), JOIN("JOIN"), ON("ON"), HAVING("HAVING");
    private final String words;

    KeyWords(String words) {
        this.words = words;
    }

    @Override
    public String toString() {
        return words;
    }
}
