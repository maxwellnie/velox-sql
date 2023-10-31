package com.maxwellnie.velox.jpa.framework.sql;

import com.maxwellnie.velox.jpa.core.enums.QueryCondition;
import com.maxwellnie.velox.jpa.core.enums.RelationShip;
import com.maxwellnie.velox.jpa.framework.sql.condition.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class SqlBuilder<T> {
    private LastFragment lastFragment;
    private WhereFragment whereFragment;
    private GroupByFragment groupByFragment;
    private HavingFragment havingFragment;
    private OrderByFragment orderByFragment;
    private List<String> columnList = new LinkedList<>();
    private LimitFragment limitFragment;

    public SqlBuilder<T> columns(String... columns) {
        columnList.addAll(Arrays.asList(columns));
        return this;
    }

    public ConditionBuilder<T> where() {
        whereFragment = new WhereFragment();
        return new ConditionBuilder<>(whereFragment, this);
    }

    public ConditionBuilder<T> having() {
        havingFragment = new HavingFragment();
        return new ConditionBuilder<>(havingFragment, this);
    }

    public SqlBuilder<T> groupBy(String... columns) {
        groupByFragment = new GroupByFragment();
        groupByFragment.getColumns().addAll(Arrays.asList(columns));
        return this;
    }

    public OrderByBuilder<T> orderBy() {
        orderByFragment = new OrderByFragment();
        return new OrderByBuilder<>(orderByFragment, this);
    }

    public SqlBuilder<T> limit(long start, long offset) {
        limitFragment = new LimitFragment();
        limitFragment.setStart(start);
        limitFragment.setOffset(offset);
        return this;
    }

    public SqlBuilder<T> last(String sql, Object... params) {
        lastFragment = new LastFragment();
        lastFragment.setNativeSql(sql);
        lastFragment.setValues(params);
        return this;
    }

    public LastFragment getLastFragment() {
        return lastFragment;
    }

    public WhereFragment getWhereFragment() {
        return whereFragment;
    }

    public GroupByFragment getGroupByFragment() {
        return groupByFragment;
    }

    public HavingFragment getHavingFragment() {
        return havingFragment;
    }

    public OrderByFragment getOrderByFragment() {
        return orderByFragment;
    }

    public LimitFragment getLimitFragment() {
        return limitFragment;
    }

    public List<String> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<String> columnList) {
        this.columnList = columnList;
    }

    public static class OrderByBuilder<T> {
        private SqlBuilder<T> parent;
        private OrderByFragment orderByFragment;

        public OrderByBuilder(OrderByFragment orderByFragment, SqlBuilder<T> parent) {
            this.orderByFragment = orderByFragment;
            this.parent = parent;
        }

        public OrderByBuilder<T> asc(String column) {
            orderByFragment.addColumn(new OrderByColumn(column, OrderByColumn.ASC));
            return this;
        }

        public OrderByBuilder<T> desc(String column) {
            orderByFragment.addColumn(new OrderByColumn(column, OrderByColumn.DESC));
            return this;
        }

        public SqlBuilder<T> build() {
            return parent;
        }
    }

    public class ConditionBuilder<T> {
        private HasConditionFragment hasConditionFragment;
        private SqlBuilder<T> parent;

        public ConditionBuilder(HasConditionFragment hasConditionFragment, SqlBuilder<T> parent) {
            this.hasConditionFragment = hasConditionFragment;
            this.parent = parent;
        }

        public ConditionBuilder<T> eq(String column, Object value) {
            this.hasConditionFragment.addConditionFragment(new NormalConditionFragment(column, QueryCondition.EQUAL, value));
            return this;
        }

        public ConditionBuilder<T> lt(String column, Object value) {
            this.hasConditionFragment.addConditionFragment(new NormalConditionFragment(column, QueryCondition.LESS_THAN, value));
            return this;
        }

        public ConditionBuilder<T> gt(String column, Object value) {
            this.hasConditionFragment.addConditionFragment(new NormalConditionFragment(column, QueryCondition.GREATER_THAN, value));
            return this;
        }

        public ConditionBuilder<T> le(String column, Object value) {
            this.hasConditionFragment.addConditionFragment(new NormalConditionFragment(column, QueryCondition.LE, value));
            return this;
        }

        public ConditionBuilder<T> ge(String column, Object value) {
            this.hasConditionFragment.addConditionFragment(new NormalConditionFragment(column, QueryCondition.GE, value));
            return this;
        }

        public ConditionBuilder<T> notNull(String column) {
            this.hasConditionFragment.addConditionFragment(new NullConditionFragment(column, QueryCondition.NOT_NULL));
            return this;
        }

        public ConditionBuilder<T> isNull(String column) {
            this.hasConditionFragment.addConditionFragment(new NullConditionFragment(column, QueryCondition.IS_NULL));
            return this;
        }

        public ConditionBuilder<T> betweenAnd(String column, Object value1, Object value2) {
            this.hasConditionFragment.addConditionFragment(new BetweenAndConditionFragment(column, value1, value2));
            return this;
        }

        public ConditionBuilder<T> in(String column, Object... params) {
            this.hasConditionFragment.addConditionFragment(new ListConditionFragment(column, params));
            return this;
        }

        public ConditionBuilder<T> notEq(String column, Object value) {
            this.hasConditionFragment.addConditionFragment(new NormalConditionFragment(column, QueryCondition.NOT_EQUAL, value));
            return this;
        }

        public ConditionBuilder<T> or() {
            this.hasConditionFragment.getConditionFragments().getLast().setRelationShip(RelationShip.OR);
            return this;
        }

        public ConditionBuilder<T> and() {
            this.hasConditionFragment.getConditionFragments().getLast().setRelationShip(RelationShip.AND);
            return this;
        }

        public ConditionBuilder<T> like(String column, Object value, int mode) {
            this.hasConditionFragment.addConditionFragment(new LikeFragment(column, value, mode));
            return this;
        }

        public ConditionBuilder<T> exists(String column) {
            this.hasConditionFragment.addConditionFragment(new ExistsFragment(column));
            return this;
        }

        public SqlBuilder<T> build() {
            return parent;
        }
    }
}

