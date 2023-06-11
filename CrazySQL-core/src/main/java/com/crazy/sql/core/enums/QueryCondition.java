package com.crazy.sql.core.enums;

/**
 * 查询条件的枚举
 */
public enum QueryCondition {
    GREATER_THAN(">"),LESS_THAN("<"),EQUAL("="),NOT_EQUAL("!="),GE(">="),IE("<="),LIKE("like");
    private String state;
    private QueryCondition(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return state;
    }
}
