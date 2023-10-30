package com.maxwellnie.velox.jpa.core.enums;

/**
 * 查询条件的枚举
 */
public enum QueryCondition {
    GREATER_THAN(">"), LESS_THAN("<"), EQUAL("="), NOT_EQUAL("<>"), GE(">="), LE("<="), LIKE("LIKE"), IS_NULL("IS NULL"), NOT_NULL("IS NOT NULL"), IN("IN"), BETWEEN("BETWEEN"), AND("AND"), EXISTS("IS EXISTS");
    private final String syntax;

    private QueryCondition(String syntax) {
        this.syntax = syntax;
    }

    @Override
    public String toString() {
        return syntax;
    }
}
