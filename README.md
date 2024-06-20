# VeloxSql

## Introduction

VeloxSql is a Java-based ORM framework with strong extensibility, which can use a small number of SQL statements and a small amount of code to achieve crud operations.

## Feature
* Lightweight
* Strong scalability
* Cache support, to ensure data consistency, buffer penetration and buffer avalanches
* Support a variety of databases, only need to write dialect adaptation classes
* Multi-data source real-time switching, the ability to dynamically switch data sources while the application is running
* Supports associated query of multiple tables
* Support paging query
* Batch processing

## Suitability

Java8+ & spring 5+, need to implement dialect interface adaptation database.

## Demo

Gitee：<a href="https://gitee.com/maxwellnie/velox-sql-demo.git">Click Me<a/><br/>
Github：<a href="https://github.com/maxwellnie/velox-sql-demo.git">Click Me<a/>

## Example
### Import and configure
pom.xml：
```xml
    <dependency>
        <groupId>io.github.maxwellnie</groupId>
        <artifactId>velox-sql-spring-boot-starter</artifactId>
        <version>1.2.1</version>
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
    table-prefix: tb_   # Indicates the prefix of the table name
    stand-table : true  # TbUser -> tb_user
    stand-column: true  # userId -> user_id
    cache: true         # cache
    is-task-queue: true # Whether to enable task queues to prevent cache avalanches
```

### Entity Class And Main Class
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
### CRUD
When we have done this, we can use the proxy object (BaseDao) built by the framework to manipulate the database. BaseDao is an interface reserved for developers to manipulate databases, providing a number of ways to manipulate databases.
The implementation of these methods is provided by the method executor, which has eight stages: checking, preprocessing, SQL generation, Statement creation, SQL execution, result processing, result caching, and closing Statement.
```java
@SpringBootTest
class TtdemoApplicationTests {
    @Resource
    BaseDao<User> userBaseDao;
    @org.junit.jupiter.api.Test
    void test(){
        /**
         * Test query
         */
        System.err.println("query result:"+userBaseDao.select(null).size());
        /**
         * Test paging
         */
        System.err.println("paging result:"+userBaseDao.selectPage(null, null).getResult());
        /**
         * Test insert
         */
        User user = new User();
        user.setLoginName("maxwell");
        user.setPassword("123456");
        System.err.println("insert result:"+userBaseDao.insert(user));
        SqlDecorator<User> sqlDecorator = new SqlDecorator<User>().where().eq("user_id", user.getUserId()).build();
        System.err.println("query result:"+userBaseDao.select(sqlDecorator));
        /**
         * Test update
         */
        user.setLoginName("????sdjks");
        System.err.println("update result:"+userBaseDao.update(user, sqlDecorator));
        System.err.println("query result:"+userBaseDao.select(sqlDecorator));
        /**
         * Test delete
         */
        System.err.println("delete result:"+userBaseDao.delete(sqlDecorator));
        System.err.println("query result:"+userBaseDao.select(sqlDecorator));
        /**
         * Test count
         */
        System.err.println("count:"+userBaseDao.count(null));

    }
}
```

### Multi-table joint search

#### In scenario one, the slave table entity object is used as the field of the primary table
master table：tb_user
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
slave table：tb_role
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
#### In scenario 2, the fields of the slave table exist in the primary table entity class as the fields of the primary table
master table：tb_user
```java
@Getter
@Setter
@Entity("tb_user")
@JoinTable(slaveTableName = "tb_role", masterTableField = "roleId", slaveTableColumn = "role_id", joinType = JoinType.LEFT, isManyToMany = false)
public class User implements Serializable {
    @PrimaryKey(strategyKey = KeyStrategyManager.JDBC_AUTO, convertor = IntegerConvertor.class)
    private int userId;
    private String loginName;
    private String password;
    private int roleId;
    @SlaveField(slaveTableName = "tb_role")// slave table field
    private String roleName;
}
```
### Multi-data source real-time switching
For multi-source real-time switching, you need to prepare an implementation of spring's multi-source transaction manager. This example uses a simple transaction manager that should not be used in production.
First, add the configuration of multiple data sources to the yaml file:
```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    first:
      url: jdbc:mysql://localhost:9999/velox-sql-demo-1?useUnicode=true&characterEncoding=UTF8
      driver-class-name: com.mysql.jdbc.Driver
      username: 
      password: 
    second:
      url: jdbc:mysql://localhost:9999/velox-sql-demo-2?useUnicode=true&characterEncoding=UTF8
      driver-class-name: com.mysql.jdbc.Driver
      username: 
      password: 
```
Then add a transaction manager and hand over the data source to the DataSourceManager:
```java
@Configuration
public class VeloxSqlConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.first")
    public DataSource dataSource() {
        return DruidDataSourceBuilder.create().build();
    }
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.second")
    public DataSource secondDataSource() {
        return DruidDataSourceBuilder.create().build();
    }
    public PlatformTransactionManager firstTransactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
    public PlatformTransactionManager secondTransactionManager() {
        return new DataSourceTransactionManager(secondDataSource());
    }
    @Bean
    public PlatformTransactionManager transactionManager1() {
        DataSourceManager.register("first", dataSource());
        DataSourceManager.register("second", secondDataSource());
        //！！！！ Be sure not to use this transaction manage
        return new ChainedTransactionManager(firstTransactionManager(), secondTransactionManager());
    }
}
```
Finally, use CurrentThreadUtils in the business tier to switch the data source with the keys stored in the DataSourceManager.
```java
@Transactional
public List<User> move(User user0, User user1, SqlDecorator<User> sqlDecorator) {
    CurrentThreadUtils.setDataSourceName("second");
    int count = userDao.insert(user1);
    System.out.println(userDao.select(null).size());
    if(count <= 0)
        throw new RuntimeException();
    CurrentThreadUtils.clearDataSourceName();
    int count1 = userDao.update(user1, sqlDecorator);
    if (count1 <= 0)
        throw new RuntimeException();
    return userDao.select(null);
}
```
### To enhance or extend the functionality of the framework
BaseDao is an interface reserved for developers to manipulate databases, providing a number of ways to manipulate databases. The implementation of these methods is provided by the method executor, which has eight stages: checking, preprocessing, SQL generation, Statement creation, SQL execution, result processing, result caching, and closing Statement.
We can implement the AbstractMethodHandler abstract class to intercept a phase of a method executor and set the sequence number of the interceptor. The larger the sequence number, the higher the priority of the interceptor and the sooner it is run. No more than 9999.
In this example, we intercept the count method of the BaseDao interface, and we intercept the openStatement method of the method execution flow, leaving the limit condition empty. Other methods are intercepted and the function of query item is realized.
```java
public class CountMethodHandler extends AbstractMethodHandler{
    //Set the interceptor serial number, the link to be intercepted, and the enhanced BaseDao method.
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
        },  new TargetMethodSignature("count", new Class[]{SqlDecorator.class}));
    }
    @Override
    public Object handle(SimpleInvocation simpleInvocation) {
        if(simpleInvocation.getArgs().length == 1){
            MetaData metaData = (MetaData) simpleInvocation.getArgs()[0];
            SqlDecorator<?> sqlDecorator = metaData.getProperty("sqlDecorator");
            if (sqlDecorator != null){
                sqlDecorator.setLimitFragment(null);
            }
            try {
                return simpleInvocation.targetMethod.invoke(simpleInvocation.getTarget(), metaData);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ExecutorException(e);
            }
        }else {
            RowSql rowSql= (RowSql) simpleInvocation.getArgs()[0];
            String sql = rowSql.getNativeSql();
            TableInfo tableInfo = (TableInfo) simpleInvocation.getArgs()[2];
            int fromIndex = sql.indexOf("FROM");
            sql = sql.substring(fromIndex);
            String count = "COUNT(*)";
            if (tableInfo.hasPk()){
                count = "COUNT("+tableInfo.getTableName()+"."+tableInfo.getPkColumn().getColumnName()+")";
            }
            sql = "SELECT"+ SqlPool.SPACE +count + SqlPool.SPACE + sql;
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
Finally, we need to add an interceptor to the framework, the following, we can realize PostJdbcContextFactoryEventListener listener (spring), such registered as a Bean. This class can listen to the build of the Context and inject an interceptor into the framework after the Context is built.
```java
public class SpringTransactionSupportInjection implements PostJdbcContextFactoryEventListener{
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(SpringTransactionSupportInjection.class);
    @Override
    public void onApplicationEvent(PostJdbcContextFactoryEvent event) {
        logger.debug(SpringSupportExecuteMethodHandler.class.getName() + " injection is successful.");
        logger.info("VeloxSql has enabled spring transaction support.");
        event.getEnvironment().addMethodHandler(new SpringSupportExecuteMethodHandler(event.getJdbcContextFactory()));//注入拦截器
    }
}
```
### ADAPTS to other databases
First, we need to implement the getDialectRowSql method of the Dialect interface:
```java
public interface Dialect {
    RowSql getDialectRowSql(RowSql rowSql, long start, long offset);
}
```
RowSql is essentially an SQL statement, so we can modify it to adapt to other databases, the following is an example, to adapt to the mysql database:
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
Next, specify the dialect in the configuration class:
```java
@Configuration
public class VeloxSqlConfig {
    public VeloxSqlConfig() {
        SingletonConfiguration.getInstance().setDialect(new MySqlDialect());
    }
}
```
### ADAPTS new type
We can convert a custom type to a database type by implementing the Convertor interface:
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
    // Database data is converted to Java type
    @Override
    public BigDecimal convert(ResultSet resultSet, int columnIndex) throws SQLException {
        BigDecimal value = resultSet.getBigDecimal(columnIndex);
        if (value == null)
            return new BigDecimal("0");
        else
            return value;
    }
    //Java object set to database
    @Override
    public void addParam(PreparedStatement preparedStatement, int index, Object param) throws SQLException {
        if(param == null)
            preparedStatement.setBigDecimal(index, null);
        else if (param instanceof String)
            preparedStatement.setBigDecimal(index, new BigDecimal((String) param));
        else if (param instanceof Number)
            preparedStatement.setBigDecimal(index, new BigDecimal(param.toString()));
        else
            throw new TypeConvertException("The param ["+param+"] is not convert to java.math.BigDecimal");
    }
    //empty
    @Override
    public BigDecimal getEmpty() {
        return new BigDecimal("0");
    }
}
```
Then specify the type converter in the column that requires the type converter:
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
    private int roleId;
    @SlaveField(slaveTableName = "tb_role")//Marked as a slave table field
    private String roleName;
}
```
# Thank you
This is the topic of my graduation thesis defense, and it got a high score. I hope this project can gradually grow into an ORM framework that is more suitable for mainstream websites. I would appreciate it if you are willing to contribute to this project.
# Contact Us
Email: maxwellnie@qq.com