# VeloxSql

## 更新日志

* 多数据源切换功能对spring进行了深度适配，可以选择低侵入式的方式实现多数据源事务
* 对Json类型进行了适配

## 升级计划

* 熔断机制
* 能够实现由实体类创建数据库表
* 引入AI，能够通过简单的聊天协助完成框架的配置、实体类和表的创建，以及对数据库操作功能的实现

## 简介

VeloxSql是一款基于Java的ORM框架，扩展性很强，可以使用少量SQL语句、少量代码实现crud操作。

## 特性

* 轻量级
* 扩展性强
* 支持缓存，能够保证数据一致性、抵御缓存穿透和缓存雪崩
* 支持多种数据库，只需要编写方言适配类
* 多数据源实时切换，能够在应用程序运行时动态切换数据源
* 支持多表关联查询
* 支持分页查询
* 批处理

## 适配性

Java8+ & spring 5+，需要实现方言接口适配数据库。

## 基本示例

Gitee仓库：<a href="https://gitee.com/maxwellnie/velox-sql-demo.git">示例代码<a/><br/>
Github仓库：<a href="https://github.com/maxwellnie/velox-sql-demo.git">示例代码<a/>

## 代码示例

### 引入和配置

pom.xml：

```xml

<dependency>
    <groupId>io.github.maxwellnie</groupId>
    <artifactId>velox-sql-spring-boot-starter</artifactId>
    <version>1.2.2</version>
</dependency>
<dependency>
<groupId>net.bytebuddy</groupId>
<artifactId>byte-buddy</artifactId>
<version>1.14.5</version>
</dependency>
```

application.yaml：

```yaml
velox-sql:
  global:
    table-prefix: tb_   # 表前缀
    stand-table: true  # 是否开启格式化表名 TbUser -> tb_user
    stand-column: true  # 是否开启格式化字段名 userId -> user_id
    cache: true         # 是否开启缓存
    is-task-queue: true # 是否开启任务队列，防止缓存雪崩
```

### 实体类和启动类

User.java

```java

@Getter
@Setter
@Entity("tb_user")
public class User extends Base implements Serializable {
    @PrimaryKey(strategyKey = KeyStrategyManager.JDBC_AUTO, convertor = IntegerConvertor.class)
    private int userId;
    private String loginName;
    private String password;
}
```

启动类

```java

@SpringBootApplication
@DaoImplConf(value = "com.example.demo.po")
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
```

### CRUD基础代码

当我们完成了上述操作，我们可以使用框架构建的代理对象(BaseDao)来操作数据库。BaseDao是为开发者预留的操作数据库的接口，提供了大量方法操作数据库。
这些方法的实现由方法执行器提供，方法执行器共有8个阶段，检查、预处理、SQL生成、Statement创建、SQL执行、结果处理、结果缓存、关闭Statement。

```java

@SpringBootTest
class TtdemoApplicationTests {
    @Resource
    BaseDao<User> userBaseDao;

    @org.junit.jupiter.api.Test
    void test() {
        /**
         * 测试查询
         */
        System.err.println("查询结果:" + userBaseDao.select(null).size());
        /**
         * 测试分页查询
         */
        System.err.println("分页结果:" + userBaseDao.selectPage(null, null).getResult());
        /**
         * 测试插入
         */
        User user = new User();
        user.setLoginName("maxwell");
        user.setPassword("123456");
        System.err.println("添加结果:" + userBaseDao.insert(user));
        SqlDecorator<User> sqlDecorator = new SqlDecorator<User>().where().eq("user_id", user.getUserId()).build();
        System.err.println("该条目:" + userBaseDao.select(sqlDecorator));
        /**
         * 测试更新
         */
        user.setLoginName("????sdjks");
        System.err.println("更新结果:" + userBaseDao.update(user, sqlDecorator));
        System.err.println("被更新条目:" + userBaseDao.select(sqlDecorator));
        /**
         * 测试删除
         */
        System.err.println("删除结果:" + userBaseDao.delete(sqlDecorator));
        System.err.println("该条目:" + userBaseDao.select(sqlDecorator));
        /**
         * 测试查询条目
         */
        System.err.println("总数据量:" + userBaseDao.count(null));

    }
}
```

### 多表联查

#### 方案一，从表实体对象作为主表的字段

主表：tb_user

```java

@Getter
@Setter
@Entity("tb_user")
public class User implements Serializable {
    @PrimaryKey(strategyKey = KeyStrategyManager.JDBC_AUTO, convertor = IntegerConvertor.class)
    private int userId;
    private String loginName;
    private String password;
    private int roleId;
    @Join(slaveTable = Role.class, masterTableField = "roleId", slaveTableField = "roleId", joinType = JoinType.LEFT, isManyToMany = false)
    private Role role;
}
```

从表：tb_role

```java

@Getter
@Setter
@Entity("tb_role")
public class Role {
    @PrimaryKey(strategyKey = KeyStrategyManager.JDBC_AUTO, convertor = IntegerConvertor.class)
    private int roleId;
    private String roleName;
}
```

#### 方案二，从表的字段作为主表的字段存在于主表实体类中

主表：tb_user

```java

@Getter
@Setter
@Entity("tb_user")
@JoinTable(slaveTableName = "tb_role", masterTableField = "roleId", slaveTableJoinColumn = "role_id", joinType = JoinType.LEFT, isManyToMany = false)
public class User implements Serializable {
    @PrimaryKey(strategyKey = KeyStrategyManager.JDBC_AUTO, convertor = IntegerConvertor.class)
    private int userId;
    private String loginName;
    private String password;
    private int roleId;
    @SlaveField(slaveTableName = "tb_role")//标记为从表字段
    private String roleName;
}
```

### 多数据源实时切换

首先，在yaml文件添加多数据源的配置：
application.yaml

```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    first:
      url: jdbc:mysql://localhost:9999/velox-sql-demo-1?useUnicode=true&characterEncoding=UTF8
      driver-class-name: com.mysql.jdbc.Driver
      username: # 你的用户名
      password: # 你的密码
    second:
      url: jdbc:mysql://localhost:9999/velox-sql-demo-2?useUnicode=true&characterEncoding=UTF8
      driver-class-name: com.mysql.jdbc.Driver
      username: # 你的用户名
      password: # 你的密码
```

VeloxSqlConfig.java

```java

@Configuration
public class VeloxSqlConfig extends VeloxAdvancedConfiguration {
    /**
     * 数据源01
     * @return DataSource
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.first")
    public DataSource dataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    /**
     * 数据源02
     * @return DataSource
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.second")
    public DataSource secondDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    /**
     * 将数据源注册到DataSourceManager中
     */
    @Override
    protected void manageDataSource() {
        DataSourceManager.register("first", dataSource());
        DataSourceManager.register("second", secondDataSource());
    }

    /**
     * 设置数据库方言
     * @param configuration
     */
    @Override
    protected void configurationChanged(com.maxwellnie.velox.sql.core.config.Configuration configuration) {
        configuration.setDialect(new MySqlDialect());
    }
}
```

对于多数据源，提供了两种方式来管理多数据源的事务。

#### 第一种方式，用户自行管理JdbcSession

在使用spring框架时通过JdbcSessionTransactionManager来管理JdbcSession，并且实现com.maxwellnie.velox.sql.core.distributed.TransactionTask接口来实现多数据源情况下事务的提交逻辑
实现事务任务

```java
/**
 * <p>代理事务真正的实现，请注意，本代理事务在多数据源情境下非链式事务，而是直接对所有待处理事务进行操作，并不关注其中某个事务成功与否</p>
 * <p>多数据源情境下不建议使用！！！！！！！！！</p>
 * @author Maxwell Nie
 */
public class NoSpringTransactionTask implements TransactionTask {
    private static final Logger logger = LoggerFactory.getLogger(NoSpringTransactionTask.class);
    /**
     * 待处理事务的元数据
     */
    private final List<MetaData> metaDataList = new LinkedList<>();

    /**
     * 添加待处理事务元数据
     * @param metaData
     */
    @Override
    public void add(MetaData metaData) {
        metaDataList.add(metaData);
    }

    /**
     * 回滚事务
     * @return
     */
    @Override
    public boolean rollback() {
        logger.debug("Transaction task start rollback.");
        int i = 0;
        for (MetaData metaData : metaDataList) {
            Connection connection = metaData.getProperty("connection");
            // 这里判断两种情况，一是当前框架事务被用户所代理（或者被框架提供的事务管理器所管理），二是当前框架事务被用户提供的spring事务管理器所管理
            if (connection != null && (CurrentJdbcSession.isOpenProxyTransaction() || DataSourceUtils.isConnectionTransactional(connection, metaData.getProperty("dataSource")))) {
                try {
                    logger.debug("rollback " + i++);
                    connection.rollback();
                } catch (Exception e) {
                    throw new JDBCConnectionException(e);
                }
            }
        }
        return true;
    }

    /**
     * 提交事务
     * @return
     */
    @Override
    public boolean commit() {
        logger.debug("Transaction task start committing.");
        int i = 0;
        for (MetaData metaData : metaDataList) {
            Connection connection = metaData.getProperty("connection");
            if (connection != null && (CurrentJdbcSession.isOpenProxyTransaction() || DataSourceUtils.isConnectionTransactional(connection, metaData.getProperty("dataSource")))) {
                try {
                    logger.debug("commit " + i++);
                    connection.commit();
                } catch (Exception e) {
                    throw new JDBCConnectionException(e);
                }
            }
        }
        return true;
    }

    /**
     * 关闭事务
     */
    @Override
    public void close() {
        logger.debug("Transaction task start closing.");
        for (MetaData metaData : metaDataList) {
            Connection connection = metaData.getProperty("connection");
            if (connection != null) {
                try {
                    if (!CurrentJdbcSession.isOpenProxyTransaction())
                        DataSourceUtils.releaseConnection(connection, metaData.getProperty("dataSource"));
                    else
                        connection.close();
                } catch (Exception e) {
                    throw new JDBCConnectionException(e);
                }
            }
        }
    }
}
```

在配置文件中指定事务任务的实现

```yaml
velox-sql:
  global:
    transaction-task-class: com.maxwellnie.velox.sql.spring.support.NoSpringTransactionTask
```

#### 第二种方式，使用JdbcSessionTransactionManager（推荐）

在配置类中添加下列代码

```java
@Bean
public PlatformTransactionManager transactionManager(JdbcSessionFactory jdbcSessionFactory){
        return new JdbcSessionTransactionManager(jdbcSessionFactory);
        }
```

最后在业务层使用CurrentThreadUtils切换数据源，切换的键是存入DataSourceManager中的键。

```java
@Transactional
public List<User> move(User user0,User user1,SqlDecorator<User> sqlDecorator){
        CurrentJdbcSession.setDataSourceName("second");
        int count=userDao.insert(user1);
        System.out.println(userDao.select(null).size());
        if(count<=0)
        throw new RuntimeException();
        CurrentJdbcSession.clearDataSourceName();
        int count1=userDao.update(user1,sqlDecorator);
        if(count1<=0)
        throw new RuntimeException();
        return userDao.select(null);
        }
```

### 对框架的功能进行增强或是扩展

BaseDao是为开发者预留的操作数据库的接口，提供了大量方法操作数据库。
这些方法的实现由方法执行器提供，方法执行器共有8个阶段，检查、预处理、SQL生成、Statement创建、SQL执行、结果处理、结果缓存、关闭Statement。
我们可以实现AbstractMethodHandler抽象类，对方法执行器的阶段进行拦截，并且设置该拦截器的序号，序号越大，拦截器的优先级越高，越先运行。最大不要超过9999。
在此例子中，我们拦截了BaseDao接口的count方法，我们拦截了方法执行流程的openStatement方法，将limit条件置空。并且拦截了其他他方法，实现了查询条目功能。

```java
public class CountMethodHandler extends AbstractMethodHandler {
    //设定拦截器序号、被拦截的环节、被增强的BaseDao方法。
    public CountMethodHandler() {
        super(999, new MethodAspect[]{
                new MethodAspect("buildRowSql", new Class[]{
                        MetaData.class
                }),
                new MethodAspect("openStatement", new Class[]{
                        RowSql.class,
                        JdbcSession.class,
                        TableInfo.class,
                        Object[].class
                })
        }, new TargetMethodSignature("count", new Class[]{SqlDecorator.class}));
    }

    @Override
    public Object handle(SimpleInvocation simpleInvocation) {
        if (simpleInvocation.getArgs().length == 1) {
            MetaData metaData = (MetaData) simpleInvocation.getArgs()[0];
            SqlDecorator<?> sqlDecorator = metaData.getProperty("sqlDecorator");
            if (sqlDecorator != null) {
                sqlDecorator.setLimitFragment(null);
            }
            try {
                return simpleInvocation.targetMethod.invoke(simpleInvocation.getTarget(), metaData);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ExecutorException(e);
            }
        } else {
            RowSql rowSql = (RowSql) simpleInvocation.getArgs()[0];
            String sql = rowSql.getNativeSql();
            TableInfo tableInfo = (TableInfo) simpleInvocation.getArgs()[2];
            int fromIndex = sql.indexOf("FROM");
            sql = sql.substring(fromIndex);
            String count = "COUNT(*)";
            if (tableInfo.hasPk()) {
                count = "COUNT(" + tableInfo.getTableName() + "." + tableInfo.getPkColumn().getColumnName() + ")";
            }
            sql = "SELECT" + SqlPool.SPACE + count + SqlPool.SPACE + sql;
            rowSql.setNativeSql(sql);
            try {
                return simpleInvocation.targetMethod.invoke(simpleInvocation.getTarget(), simpleInvocation.getArgs());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ExecutorException(e);
            }
        }
    }
}
```

最后，我们需要将拦截器添加到框架中，如下，我们可以实现PostJdbcSessionFactoryEventListener(spring的监听器)
，将此类注册为Bean。此类可以监听Context的构建，并且在Context构建后将拦截器注入到框架中。

```java
public class SpringTransactionSupportInjection implements PostJdbcSessionFactoryEventListener {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(SpringTransactionSupportInjection.class);

    @Override
    public void onApplicationEvent(PostJdbcSessionFactoryEvent event) {
        logger.debug(SpringSupportExecuteMethodHandler.class.getName() + " injection is successful.");
        logger.info("VeloxSql has enabled spring transaction support.");
        event.getEnvironment().addMethodHandler(new SpringSupportExecuteMethodHandler(event.getJdbcSessionFactory()));//注入拦截器
    }
}
```

### 适配其他数据库

首先，我们需要实现Dialect接口的getDialectRowSql方法：

```java
public interface Dialect {
    RowSql getDialectRowSql(RowSql rowSql, long start, long offset);
}
```

RowSql本质是一条SQL语句，所以我们可以对其修改以适配其他数据库，下面是一个示例，适配mysql数据库：

```java
public class MySqlDialect implements Dialect {
    @Override
    public RowSql getDialectRowSql(RowSql rowSql, long start, long offset) {
        String sql = rowSql.getNativeSql();
        sql = sql + SqlPool.SPACE + "LIMIT " + start + ", " + offset;
        rowSql.setNativeSql(sql);
        return rowSql;
    }
}
```

接着，在配置类指定方言：

```java

@Configuration
public class VeloxSqlConfig {
    public VeloxSqlConfig() {
        SingletonConfiguration.getInstance().setDialect(new MySqlDialect());
    }
}
```

### 适配新的类型

我们可以通过实现Convertor接口，将自定义类型转换为数据库类型：

```java
public class BigDecimalConvertor implements TypeConvertor<BigDecimal> {
    @Override
    public BigDecimal convert(ResultSet resultSet, String column) throws SQLException {
        BigDecimal value = resultSet.getBigDecimal(column);
        if (value == null)
            return new BigDecimal("0");
        else
            return value;
    }

    //数据库数据转换到Java类型
    @Override
    public BigDecimal convert(ResultSet resultSet, int columnIndex) throws SQLException {
        BigDecimal value = resultSet.getBigDecimal(columnIndex);
        if (value == null)
            return new BigDecimal("0");
        else
            return value;
    }

    //Java对象设置到数据库
    @Override
    public void addParam(PreparedStatement preparedStatement, int index, Object param) throws SQLException {
        if (param == null)
            preparedStatement.setBigDecimal(index, null);
        else if (param instanceof String)
            preparedStatement.setBigDecimal(index, new BigDecimal((String) param));
        else if (param instanceof Number)
            preparedStatement.setBigDecimal(index, new BigDecimal(param.toString()));
        else
            throw new TypeConvertException("The param [" + param + "] is not convert to java.math.BigDecimal");
    }

    //空值
    @Override
    public BigDecimal getEmpty() {
        return new BigDecimal("0");
    }
}
```

然后在需要这个类型转换器的列指定类型转换器：

```java

@Getter
@Setter
@Entity("tb_user")
@JoinTable(slaveTableName = "tb_role", masterTableField = "roleId", slaveTableColumn = "role_id", joinType = JoinType.LEFT, isManyToMany = false)
public class User implements Serializable {
    //指定类型转换器
    @PrimaryKey(strategyKey = KeyStrategyManager.JDBC_AUTO, convertor = IntegerConvertor.class)
    private int userId;
    private String loginName;
    private String password;
    @SlaveField(slaveTableName = "tb_role")//标记为从表字段
    private int roleId;
    @SlaveField(slaveTableName = "tb_role")//标记为从表字段
    private String roleName;
}
```

# 致谢

这是我大学毕设答辩的题目，并且它获得了一个很高的分数，我希望这个项目能逐渐成长为更适合主流网站的ORM框架，如果你愿意为此项目做贡献，我将不胜感激。

# 联系我们

邮箱：maxwellnie@qq.com