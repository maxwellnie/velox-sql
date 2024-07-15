package com.maxwellnie.velox.sql.core.natives.dao;

import com.maxwellnie.velox.sql.core.natives.jdbc.sql.SqlPool;
import com.maxwellnie.velox.sql.core.natives.jdbc.sql.creator.SqlCreator;
import com.maxwellnie.velox.sql.core.utils.java.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public class SqlDecorator<T> {
    private WhereSql whereSql;
    private GroupBySql groupByFragment;
    private HavingSql havingFragment;
    private OrderBySql orderByFragment;
    private LimitSql limitFragment;
    private BaseSql applySql;
    private BaseSql lastSql;

    public ConditionBuilder<T> where() {
        whereSql = new WhereSql();
        return new ConditionBuilder<>(whereSql.getInlineSql());
    }

    public ConditionBuilder<T> having() {
        havingFragment = new HavingSql();
        return new ConditionBuilder<>(havingFragment.getInlineSql());
    }

    public SqlDecorator<T> groupBy(String... columns) {
        groupByFragment = new GroupBySql();
        String sql = Arrays.toString(columns);
        groupByFragment.setSql(SqlCreator.create(groupByFragment.getSql(), sql.substring(1, sql.length() - 1)));
        return this;
    }

    public OrderByBuilder<T> orderBy() {
        orderByFragment = new OrderBySql();
        return new OrderByBuilder<>(orderByFragment);
    }

    public SqlDecorator<T> limit(long start, long offset) {
        limitFragment = new LimitSql();
        limitFragment.setStart(start);
        limitFragment.setOffset(offset);
        return this;
    }

    public WhereSql getWhereFragment() {
        return whereSql;
    }

    public GroupBySql getGroupByFragment() {
        return groupByFragment;
    }

    public void setGroupByFragment(GroupBySql groupByFragment) {
        this.groupByFragment = groupByFragment;
    }

    public HavingSql getHavingFragment() {
        return havingFragment;
    }

    public void setHavingFragment(HavingSql havingFragment) {
        this.havingFragment = havingFragment;
    }

    public OrderBySql getOrderByFragment() {
        return orderByFragment;
    }

    public void setOrderByFragment(OrderBySql orderByFragment) {
        this.orderByFragment = orderByFragment;
    }

    public void setWhereSql(WhereSql whereSql) {
        this.whereSql = whereSql;
    }

    public LimitSql getLimitFragment() {
        return limitFragment;
    }

    public void setLimitFragment(LimitSql limitFragment) {
        this.limitFragment = limitFragment;
    }

    /**
     * 在整个构建好的Sql语句的最后方加入一段Sql，若使用MySQL这段语句在分页方法中将被添加到LIMIT的前方，要保持其在最后方，请使用last方法注入。
     *
     * @param sql
     * @param params
     * @return
     */
    public SqlDecorator<T> apply(String sql, Object... params) {
        applySql = new BaseSql() {
        };
        applySql.setSql(sql);
        if (params != null && params.length > 0)
            applySql.addParams(params);
        return SqlDecorator.this;
    }

    /**
     * 在整个构建好的Sql语句的最后方加入一段Sql。
     *
     * @param sql
     * @param params
     * @return
     */
    public SqlDecorator<T> last(String sql, Object... params) {
        lastSql = new BaseSql() {
        };
        lastSql.setSql(sql);
        if (params != null && params.length > 0)
            lastSql.addParams(params);
        return SqlDecorator.this;
    }

    public BaseSql getApplySql() {
        return applySql;
    }

    public BaseSql getLastSql() {
        return lastSql;
    }

    public class OrderByBuilder<T> {
        private OrderBySql orderByFragment;

        public OrderByBuilder(OrderBySql orderByFragment) {
            this.orderByFragment = orderByFragment;
        }

        public OrderByBuilder<T> asc(String column) {
            orderByFragment.addParamInfo(new OrderBySql.ParamInfo(column, SqlPool.ORDER_BY_ASC));
            return this;
        }

        public OrderByBuilder<T> desc(String column) {
            orderByFragment.addParamInfo(new OrderBySql.ParamInfo(column, SqlPool.ORDER_BY_DESC));
            return this;
        }

        public SqlDecorator<T> build() {
            StringBuilder sql = new StringBuilder();
            for (int i = 0; i < orderByFragment.getParamInfos().size(); i++) {
                OrderBySql.ParamInfo paramInfo = orderByFragment.getParamInfos().get(i);
                sql.append(paramInfo.getColumn()).append(SqlPool.SPACE).append(paramInfo.getOrder());
                if (i < orderByFragment.getParamInfos().size() - 1) {
                    sql.append(",");
                }
            }
            orderByFragment.setSql(SqlCreator.create(orderByFragment.getSql(), sql.toString()));
            return (SqlDecorator<T>) SqlDecorator.this;
        }
    }

    public class ConditionBuilder<T> {
        List<WhereSql.InlineSql> inlineSql;

        public ConditionBuilder(List<WhereSql.InlineSql> inlineSql) {
            this.inlineSql = inlineSql;
        }

        public ConditionBuilder<T> eq(String column, Object value) {
            WhereSql.InlineSql inlineSql = new WhereSql.InlineSql();
            inlineSql.setSql(column + SqlPool.EQ);
            inlineSql.addParam(value);
            this.inlineSql.add(inlineSql);
            return this;
        }

        public ConditionBuilder<T> lt(String column, Object value) {
            WhereSql.InlineSql inlineSql = new WhereSql.InlineSql();
            inlineSql.setSql(column + SqlPool.LT);
            inlineSql.addParam(value);
            this.inlineSql.add(inlineSql);
            return this;
        }

        public ConditionBuilder<T> gt(String column, Object value) {
            WhereSql.InlineSql inlineSql = new WhereSql.InlineSql();
            inlineSql.setSql(column + SqlPool.GT);
            inlineSql.addParam(value);
            this.inlineSql.add(inlineSql);
            return this;
        }

        public ConditionBuilder<T> le(String column, Object value) {
            WhereSql.InlineSql inlineSql = new WhereSql.InlineSql();
            inlineSql.setSql(column + SqlPool.LTE);
            inlineSql.addParam(value);
            this.inlineSql.add(inlineSql);
            return this;
        }

        public ConditionBuilder<T> ge(String column, Object value) {
            WhereSql.InlineSql inlineSql = new WhereSql.InlineSql();
            inlineSql.setSql(column + SqlPool.GTE);
            inlineSql.addParam(value);
            this.inlineSql.add(inlineSql);
            return this;
        }

        public ConditionBuilder<T> notNull(String column) {
            WhereSql.InlineSql inlineSql = new WhereSql.InlineSql();
            inlineSql.setSql(column + SqlPool.NOT_NULL);
            this.inlineSql.add(inlineSql);
            return this;
        }

        public ConditionBuilder<T> isNull(String column) {
            WhereSql.InlineSql inlineSql = new WhereSql.InlineSql();
            inlineSql.setSql(column + SqlPool.IS_NULL);
            this.inlineSql.add(inlineSql);
            return this;
        }

        public ConditionBuilder<T> betweenAnd(String column, Object value1, Object value2) {
            WhereSql.InlineSql inlineSql = new WhereSql.InlineSql();
            inlineSql.setSql(column + SqlPool.BETWEEN_AND);
            inlineSql.addParams(new Object[]{value1, value2});
            this.inlineSql.add(inlineSql);
            return this;
        }

        public ConditionBuilder<T> in(String column, Object... params) {
            WhereSql.InlineSql inlineSql = new WhereSql.InlineSql();
            inlineSql.setSql(column + SqlCreator.create(SqlPool.IN, StringUtils.buildParamsTuple(params.length)));
            inlineSql.addParams(params);
            this.inlineSql.add(inlineSql);
            return this;
        }

        public ConditionBuilder<T> notEq(String column, Object value) {
            WhereSql.InlineSql inlineSql = new WhereSql.InlineSql();
            inlineSql.setSql(column + SqlPool.NE);
            inlineSql.addParam(value);
            this.inlineSql.add(inlineSql);
            return this;
        }

        public ConditionBuilder<T> or() {
            WhereSql.InlineSql inlineSql = new WhereSql.InlineSql(true);
            inlineSql.setSql(SqlPool.OR);
            this.inlineSql.add(inlineSql);
            return this;
        }

        public ConditionBuilder<T> and() {
            WhereSql.InlineSql inlineSql = new WhereSql.InlineSql(true);
            inlineSql.setSql(SqlPool.AND);
            this.inlineSql.add(inlineSql);
            return this;
        }

        /**
         * @param column
         * @param value
         * @param mode   0 %s, 1 %s%, 2 s%
         * @return
         */
        public ConditionBuilder<T> like(String column, Object value, int mode) {
            WhereSql.InlineSql inlineSql = new WhereSql.InlineSql();
            String like;
            if (mode == 0) {
                like = "%" + value;
            } else if (mode == 1) {
                like = "%" + value + "%";
            } else if (mode == 2) {
                like = value + "%";
            } else {
                throw new IllegalArgumentException("mode must be 0, 1, 2");
            }
            inlineSql.setSql(column + SqlPool.LIKE);
            inlineSql.addParam(like);
            this.inlineSql.add(inlineSql);
            return this;
        }

        public SqlDecorator<T> build() {
            return (SqlDecorator<T>) SqlDecorator.this;
        }
    }
}

