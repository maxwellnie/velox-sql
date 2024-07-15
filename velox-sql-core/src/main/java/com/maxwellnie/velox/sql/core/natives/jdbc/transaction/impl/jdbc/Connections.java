package com.maxwellnie.velox.sql.core.natives.jdbc.transaction.impl.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

/**
 * 当前线程使用过的所有连接对象的集合
 *
 * @author Maxwell Nie
 */
public class Connections {
    private LinkedList<DataSourceAndConnection> connections;

    public Connections() {
        connections = new LinkedList<>();
    }

    public void add(DataSource dataSource, Connection connection) {
        connections.add(new DataSourceAndConnection(dataSource, connection));
    }

    /**
     * 获取最后一个连接
     *
     * @return
     */
    public Connection getConnection() {
        if (connections.isEmpty())
            return null;
        return connections.getLast().getConnection();
    }

    /**
     * 获取最后一个连接和其对应的数据源对象
     *
     * @return
     */
    public DataSourceAndConnection get() {
        if (connections.isEmpty())
            return null;
        return connections.getLast();
    }

    /**
     * 获取所有使用过的连接
     *
     * @return
     */
    public List<DataSourceAndConnection> all() {
        return connections;
    }

    /**
     * 是否为空
     *
     * @return
     */
    public boolean isEmpty() {
        return connections.isEmpty();
    }

    /**
     * 清空
     */
    public void clear() {
        connections.clear();
    }

    /**
     * 数据源和连接对象
     */
    public static class DataSourceAndConnection {
        private DataSource dataSource;
        private Connection connection;
        private Integer level;

        public DataSourceAndConnection(DataSource dataSource, Connection connection) {
            this.dataSource = dataSource;
            this.connection = connection;
        }

        public DataSource getDataSource() {
            return dataSource;
        }

        public Connection getConnection() {
            return connection;
        }

        public void setDataSource(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public void setConnection(Connection connection) {
            this.connection = connection;
        }

        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }
    }
}
