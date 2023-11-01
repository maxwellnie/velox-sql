# VeloxJPA

### 什么是VeloxJPA？

VeloxJPA是一款基于Java的，符合JPA规范的数据持久化框架，扩展性很强，可以无需书写SQL语句和声明Dao接口，由VeloxJPA为你实现Dao层的功能，VeloxJPA核心提供基本的框架，你可以根据自身需要，自定义或者是优化SQL，以此增强JPA的功能，通过自定义开发产生的模块称之为velox-jpa-xxx-template。
### 适配性

目前velox-jpa只适配了mysql5+ & Java8+ & spring 5+，你可以根据自身需求编写新的，以适配不同的数据库。

### 使用文档

#### java原生快速上手:
导入依赖：
```xml
<dependencies>
    <dependency>
        <groupId>io.github.maxwellnie</groupId>
        <artifactId>velox-jpa-core</artifactId>
        <version>1.1</version>
    </dependency>
      <dependency>
        <groupId>io.github.maxwellnie</groupId>
        <artifactId>velox-jpa-core-template</artifactId>
        <version>1.1</version>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>5.1.8</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>1.6.1</version>
    </dependency>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.2.6</version>
    </dependency>
</dependencies>
```
```java
import com.maxwellnie.velox.jpa.core.dao.support.SqlBuilder;
import com.maxwellnie.velox.jpa.core.dao.support.env.Environment;
import com.maxwellnie.velox.jpa.core.cahce.impl.LRUCache;
import com.maxwellnie.velox.jpa.core.config.simple.VeloxJpaConfig;
import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContextFactory;
import com.maxwellnie.velox.jpa.core.jdbc.context.SimpleContextFactory;
import com.maxwellnie.velox.jpa.core.jdbc.pool.impl.SimpleConnectionPool;
import com.maxwellnie.velox.jpa.core.jdbc.transaction.impl.jdbc.JdbcTransactionFactory;
import com.maxwellnie.velox.jpa.core.template.dao.TemplateDao;

import java.sql.SQLException;

public class Tests {
    public static void main(String[] args) throws SQLException {
        //配置数据源
        SimpleConnectionPool simpleConnectionPool = new SimpleConnectionPool();
        simpleConnectionPool.setDriverClassName("com.mysql.jdbc.Driver");
        simpleConnectionPool.setUsername("root");
        simpleConnectionPool.setPassword("123456");
        simpleConnectionPool.setUrl("jdbc:mysql://localhost:3307/bounddatabase");
        //配置针对普通Java程序的配置类VeloxJpaConfig
        VeloxJpaConfig veloxJpaConfig = VeloxJpaConfig.getInstance();
        veloxJpaConfig.setCache(true);
        veloxJpaConfig.setCacheClassName(LRUCache.class.getName());
        veloxJpaConfig.setTablePrefix("tb_");
        veloxJpaConfig.setStandColumn(true);
        veloxJpaConfig.setStandTable(true);
        //初始化JdbcContext生产环境环境
        Environment environment=new Environment(new JdbcTransactionFactory(),simpleConnectionPool, veloxJpaConfig);
        environment.addDaoImpl(User.class);
        //JdbcContext工厂
        JdbcContextFactory jdbcContextFactory=new SimpleContextFactory(environment);
        JdbcContext jdbcContext= jdbcContextFactory.produce(false);
        //获取实例
        TemplateDao<User> daoImpl = (TemplateDao<User>) environment.getDaoImplFactory(User.class).produce(jdbcContext);
        //查询数据
        System.out.println(daoImpl.queryAll(new SqlBuilder<User>().where().eq("user_id",32).build()));
        jdbcContext.commit();
        System.out.println(daoImpl.queryAll(new SqlBuilder<User>().where().eq("user_id",32).build()));
    }
}
```


User实体：
```java
import com.maxwellnie.vleox.jpa.core.annotation.Entity;
import com.maxwellnie.vleox.jpa.core.annotation.Column;
import com.maxwellnie.vleox.jpa.core.annotation.PrimaryKey;
import com.maxwellnie.vleox.jpa.core.enums.PrimaryMode;

import java.util.Date;

/**
 * @author Maxwell Nie
 */
@Entity("tb_user")
public  class User {
    @PrimaryKey(strategyKey = "jdbc_auto")//KeyStrateyManager.JDBC_AUTO
    public int userId;
    public String loginName;
    public String password;
    private String userName;
    private String roleName;
    private String rights;
    private String iconPath;
    private boolean sex;
    private Date lastTime;

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", loginName='" + loginName + '\'' +
                ", password='" + password + '\'' +
                ", userName='" + userName + '\'' +
                ", roleName='" + roleName + '\'' +
                ", rights='" + rights + '\'' +
                ", iconPath='" + iconPath + '\'' +
                ", sex=" + sex +
                ", lastTime=" + lastTime +
                '}';
    }
}
```
#### spring快速上手:
依赖：
```xml
 <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>5.3.20</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>5.3.20</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>5.3.20</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.8</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.1.2</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.2.16</version>
        </dependency>
        <dependency>
            <groupId>io.github.maxwellnie</groupId>
            <artifactId>velox-jpa-spring</artifactId>
            <version>1.0</version>
        </dependency>
        <dependency>
            <groupId>io.github.maxwellnie</groupId>
            <artifactId>velox-jpa-core-template</artifactId>
            <version>1.1</version>
        </dependency>
        <dependency>
            <groupId>net.bytebuddy</groupId>
            <artifactId>byte-buddy</artifactId>
            <version>1.14.5</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
```
xml配置文件：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:task="http://www.springframework.org/schema/task"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
		http://www.springframework.org/schema/beans/spring-beans-5.0.xsd
		http://www.springframework.org/schema/mvc
		http://www.springframework.org/schema/context
		http://www.springframework.org/schema/context/spring-context-5.0.xsd
		http://www.springframework.org/schema/aop
		http://www.springframework.org/schema/aop/spring-aop-5.0.xsd
		http://www.springframework.org/schema/tx
		http://www.springframework.org/schema/tx/spring-tx-5.0.xsd
		http://www.springframework.org/schema/task
   		http://www.springframework.org/schema/task/spring-task-5.0.xsd">
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3307/bounddatabase?useUnicode=true&amp;characterEncoding=UTF8"/>
        <property name="username" value="root"/>
        <property name="password" value="123456"/>
    </bean>
    <bean id="jdbcContextFactoryBean" class="com.velox.jpa.spring.config.bean.JdbcContextFactoryBean">
        <property name="cache" value="true"/>
        <property name="dataSource" ref="dataSource"/>
        <property name="standColumn" value="true"/>
        <property name="standTable" value="true"/>
        <property name="tablePrefix" value="tb_"/>
    </bean>
    <bean id="methodMapRegister" class="com.velox.jpa.spring.bean.DaoImplRegister">
        <property name="packagePaths" value="com.example.po"/>
    </bean>
</beans>
```
Test类：
```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring.xml"})
public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}
	@Resource
	private TemplateDao<User> userTemplateDao;
	@org.junit.Test
	public void test(){
		boolean b;
		System.out.println(userTemplateDao.queryAll(new SqlBuilder<User>().where().eq("user_id",98).build()));
	}
}
```
#### springboot快速上手:
依赖：
```xml
 <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
            <version>${spring-boot.version}</version>

        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <version>${spring-boot.version}</version>
            <scope>test</scope>
        </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>mysql</groupId>
                <artifactId>mysql-connector-java</artifactId>
                <version>5.1.8</version>
            </dependency>
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <optional>true</optional>
                <version>1.18.28</version>
            </dependency>
            <dependency>
                <groupId>io.github.maxwellnie</groupId>
                <artifactId>velox-jpa-spring-boot-starer</artifactId>
                <version>1.1</version>
            </dependency>
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>druid</artifactId>
                <version>1.2.16</version>
            </dependency>
        <dependency>
            <groupId>io.github.maxwellnie</groupId>
            <artifactId>velox-jpa-core-template</artifactId>
            <version>1.1</version>
        </dependency>
        <dependency>
            <groupId>net.bytebuddy</groupId>
            <artifactId>byte-buddy</artifactId>
            <version>1.14.5</version>
        </dependency>
        </dependencies>
```
application.yml:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3307/bounddatabase?useUnicode=true&characterEncoding=UTF8
    driver-class-name: com.mysql.jdbc.Driver
    password: 123456
    username: root
    type: com.alibaba.druid.pool.DruidDataSource
logging:
  level:
    root: debug
velox-jpa:
  tablePrefix: tb_
  cache: true
  standTable : true
  standColumn: true
```
启动类：
```java
@SpringBootApplication
@DaoImplConf(value = "com.example.ttdemo.po")
public class TtdemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TtdemoApplication.class, args);
    }

}
```
User实体：
```java
package com.example.ttdemo.po;

import com.maxwellnie.velox.jpa.core.annotation.Entity;
import com.maxwellnie.velox.jpa.core.annotation.PrimaryKey;
import com.maxwellnie.velox.jpa.core.enums.PrimaryMode;
import com.maxwellnie.velox.jpa.core.manager.KeyStrategyManager;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@Entity
public class User {
    @PrimaryKey(strategyKey = KeyStrategyManager.JDBC_AUTO)
    private int userId;
    private String loginName;
    private String password;

}
```
Test:
```java
package com.example.ttdemo;

import com.example.ttdemo.po.User;
import com.maxwellnie.velox.jpa.core.dao.support.SqlBuilder;
import com.maxwellnie.velox.jpa.core.template.dao.TemplateDao;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.sql.SQLException;

@SpringBootTest
class TtdemoApplicationTests {
    @Resource
    TemplateDao<User> userTemplateDao;
    @Test
    void contextLoads() throws SQLException {
        System.out.println(userTemplateDao);
        User user= userTemplateDao.queryOne(new SqlBuilder<User>().where().eq("user_id",49).build());
        System.out.println(userTemplateDao.queryOne(new SqlBuilder<User>().where().eq("user_id",49).build()));
        System.out.println(userTemplateDao.queryOne(new SqlBuilder<User>().where().eq("user_id",490).build()));
        System.out.println(userTemplateDao.queryOne(new SqlBuilder<User>().where().eq("user_id",234).build()));
    }

}
```
sql:
```sql
USE DATABASE bounddatabase;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_bound
-- ----------------------------
DROP TABLE IF EXISTS `tb_bound`;
CREATE TABLE `tb_bound`  (
  `bound_id` int(11) NOT NULL AUTO_INCREMENT,
  `bound_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `count` int(11) NOT NULL,
  PRIMARY KEY (`bound_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`  (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `login_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `rights` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `icon_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `sex` tinyint(255) NULL DEFAULT NULL,
  `last_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE,
  INDEX `a`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 0 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
```
#### 开发者

如果你是一个想要丰富VeloxJpa功能的开发者，你需要了解VeloxJpa的特点、架构。

VeloxJpa是一个高度自定义的框架，你可以创造属于你的velox-jpa-template并将它分享到互联网。

VeloxJpa分为以下结构：

##### Dao<T>接口

这个是开发者需要提供的接口，这个接口中需要编写对应的操作数据库开放的方法

```
编写好接口后，你需要指定接口中哪些方法是操作数据库的方法，哪些是默认方法，在这里我们提供了一种注册操作数据库方法的注解：
@RegisterMethod(value=Class<? extends Executor>)，这个注解中，你需要指定你所实现的Executor。
```

##### Executor接口

这个接口用于实现Dao被注册方法的逻辑实现

```java

/**
 * 方法执行器，执行被代理的方法
 *
 * @author Maxwell Nie
 */
public interface Executor {
    /**
     * 执行被代理的方法
     *
     * @param tableInfo
     * @param context
     * @param cache
     * @param daoImplHashCode
     * @param args
     * @return 操作结果
     */
    Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object, Object> cache, String daoImplHashCode, Object[] args);
}


你需要实现对应的逻辑，例如，编写一个可以根据SqlBuilder创建的条件删除实体对应数据表中的条目：

@DaoImplDeclared
public interface MyDao<T>{
	@RegisterMethod(DeleteOneExecutor.class)
	int deleteOne(SqlBuilder<T> sqlbuilder);
}


那么需要编写对应的DeleteOneExecutor：


public class DeleteOneExecutor implements Executor{
    @Override
    public Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object, Object> cache, String daoImplHashCode, Object[] args) {
        /**
         * 获取事务
         */
        Transaction transaction = context.getTransaction();
        /**
         * SqlBuilder
         */
        SqlBuilder o = args[0] == null ? null : (SqlBuilder) args[0];
        try {
            /**
             * 开始执行sql语句
             */
            Object result = openStatement(transaction.getConnection(), tableInfo, o);
            flushCache(result, null, cache, context.getDirtyManager(), !context.getAutoCommit());
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    openStatement(....){
    	//处理sql
    }
    flushCache(....){
    	//刷新缓存
    }
    getDeleteSqlStatement(....){
    	//获取完整的sql语句
    }
}
```
1.1版本后，强烈建议使用velox-jpa-framework提供的基础框架开发Executor。
##### ConvertorManager 

TypeConvertor的管理器，可以注册、或者获取对应Java数据类型的转换器，用于查询数据库数据后转换从JDBC API获取到的数据。例如将java.sql.Date转换为java.util.Date

##### KeyStrategyManager 

 KeyStrategy的管理器，可以注册、或者获取对应名字的KeyStrategy，用于添加数据时获取主键值，和返回主键值。

##### MethodMappedManager 

Executor和被注册方法的映射管理，最好不要在运行时注册映射到这个管理器，可能会产生未知的后果。

##### TypeConvertor<T> 类型转换器

当JDBC从数据库取出数据后，会调用ResultSet的getObject方法获取数据，这个转换器的意义就在于可以将JDBC API 提供的对象转换为我们想要的对象，如果结合@Column注解，这种方式将会类似于ORM。

```java
/**
 * @author Maxwell Nie
 */
public interface TypeConvertor<T> {
    T convert(ResultSet resultSet, String column) throws SQLException;
    T convert(ResultSet resultSet, int columnIndex) throws SQLException;
}

例如，我们需要将JDBC API查询出的Date转换，从类型java.sql.TimeStamp转换为java.util.Date，我们可以编写DateConvertor：
/**
 * @author Maxwell Nie
 */
public class DateConvertor implements TypeConvertor<Date> {
    @Override
    public Date convert(ResultSet resultSet, String column) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp(column);
        if(timestamp!=null)
            return new Date(timestamp.getTime());
        else
            return null;
    }

    @Override
    public Date convert(ResultSet resultSet, int columnIndex) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp(columnIndex);
        if(timestamp!=null)
            return new Date(timestamp.getTime());
        else
            return null;
    }
}

```

##### Generator主键值生成器

用于在添加前生成主键值。

```java
例如没有生成器的实例
public class NoGenerator implements KeyGenerator {

    @Override
    public Object nextKey() {
        return null;
    }
}
```

##### KeySelector主键值查询器

用于添加数据后将主键值赋值给实体对象

```java
Jdbc自增方式的主键值查询器
public class JdbcSelector implements KeySelector {
    @Override
    public Object selectGeneratorKey(Object param) {
        if (param == null)
            return null;
        Statement statement = (Statement) param;
        try {
            return ResultSetUtils.getAutoIncrementKey(statement.getGeneratedKeys());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
```

##### JdbcContext JDBC环境

Transaction和脏数据管理器的组合实体，可以提交、回滚数据。这个类并不是可操作数据库的实体类，只能更改脏数据内容和控制事务，可以理解为缓存事务和Jdbc事务的集合体。

```java
/**
 * 执行器操作数据库的必要环境
 *
 * @author Maxwell Nie
 */
public interface JdbcContext extends Closeable {
    /**
     * 获取自动提交
     *
     * @return
     */
    boolean getAutoCommit();

    /**
     * 设置自动提交
     *
     * @param flag
     */
    void setAutoCommit(boolean flag);

    /**
     * 关闭环境
     */
    void close();

    /**
     * 提交操作
     */
    void commit();

    /**
     * 回滚操作
     */
    void rollback();

    /**
     * 获取事务对象
     *
     * @return
     */
    Transaction getTransaction();

    /**
     * 获取脏数据管理器
     *
     * @return
     */
    CacheDirtyManager getDirtyManager();
}
```
### 1.1版本

#### velox-jpa-framewok

velox-jpa-framework提供了Sql执行、构建、缓存、主键策略的基本逻辑，定义了Executor的执行阶段（执行周期），velox-jpa-framework将通过委托机制来执行Execute各个阶段的代码。

##### ExecutorDelegate

```java

package com.maxwellnie.velox.jpa.framework.proxy.executor.cycle;

import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.proxy.executor.Executor;
import com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper;
import com.maxwellnie.velox.jpa.framework.exception.ExecutorException;
import com.maxwellnie.velox.jpa.framework.sql.SimpleSqlFragment;
import com.maxwellnie.velox.jpa.framework.utils.ErrorUtils;
import org.slf4j.Logger;

import java.sql.Connection;

/**
 * 代理了Executor，在不同的阶段委派ExecuteCycle的不同方法来执行并获取结果。
 *
 * @author Maxwell Nie
 */
public class ExecutorDelegate implements Executor {
    private final ExecuteCycle concrete;
    private final Logger logger;
    private final Object errorResult;

    public ExecutorDelegate(ExecuteCycle concrete) {
        this.concrete = concrete;
        this.logger = concrete.getLogger();
        this.errorResult = concrete.errorResult;
    }

    /**
     * 所有的Executor都应该遵循这个规范。
     *
     * @param tableInfo
     * @param context
     * @param cache
     * @param daoImplHashCode
     * @param args
     * @return
     */
    @Override
    public Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object, Object> cache, String daoImplHashCode, Object[] args) {
        try {
            /**
             * 检查环境阶段。
             */
            concrete.checkExecuteCondition(tableInfo, context, cache, daoImplHashCode, args);
            /**
             * 获取连接阶段。
             */
            Connection connection = concrete.doConnection(context);
            /**
             * sql创建阶段。
             */
            SimpleSqlFragment sqlFragment = concrete.getNativeSql(args, tableInfo);
            logger.debug("SQL ### : " + sqlFragment.getNativeSql());
            logger.debug("PARAM # : " + sqlFragment.getParams());
            /**
             * Statement实例化阶段。
             */
            StatementWrapper statementWrapper = concrete.openStatement(sqlFragment, connection, tableInfo, args);
            /**
             * sql执行阶段。
             */
            long startTime = System.currentTimeMillis();
            ExecuteCycle.SqlResult sqlResult = concrete.executeSql(statementWrapper, sqlFragment, daoImplHashCode, cache);
            logger.debug("SQL EXECUTED | TIME: " + (System.currentTimeMillis() - startTime) + "ms.");
            /**
             * 缓存刷新阶段。
             */
            concrete.flushCache(sqlResult, cache, context.getDirtyManager(), !context.getAutoCommit());
            return sqlResult.getResult();
        } catch (ExecutorException e) {
            logger.error(ErrorUtils.getSimpleExceptionLog(e));
            return errorResult;
        }
    }
}

```

##### BaseExecutor

实现了方法的执行逻辑、参数的检查、实例化Statement的基本逻辑、刷新缓存的完整逻辑和sql执行的部分逻辑。

```java
package com.maxwellnie.velox.jpa.framework.proxy.executor;

import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.cahce.dirty.CacheDirtyManager;
import com.maxwellnie.velox.jpa.core.cahce.key.CacheKey;
import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.proxy.executor.Executor;
import com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper;
import com.maxwellnie.velox.jpa.framework.exception.ExecutorException;
import com.maxwellnie.velox.jpa.framework.proxy.executor.cycle.ExecuteCycle;
import com.maxwellnie.velox.jpa.framework.sql.SimpleSqlFragment;
import com.maxwellnie.velox.jpa.framework.utils.ErrorUtils;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper.*;

/**
 * 基本的对SQL方法执行器
 *
 * @author Maxwell Nie
 */
public abstract class BaseExecutor extends ExecuteCycle implements Executor {
    protected final Logger logger;

    public BaseExecutor(Logger logger, Object errorResult) {
        this.logger = logger;
        this.errorResult = errorResult;
    }

    /**
     * 所有的Executor都应该遵循这个规范。
     * @param tableInfo
     * @param context
     * @param cache
     * @param daoImplHashCode
     * @param args
     * @return
     */
    @Override
    public Object execute(TableInfo tableInfo, JdbcContext context, Cache<Object, Object> cache, String daoImplHashCode, Object[] args) {
        try {
            checkArgs(args);
            Connection connection = checkContext(context, tableInfo);
            SimpleSqlFragment sqlFragment = getNativeSql(args, tableInfo);
            logger.debug("SQL ### : "+sqlFragment.getNativeSql());
            logger.debug("PARAM # : "+sqlFragment.getParams());
            StatementWrapper statementWrapper = openStatement(sqlFragment, connection, tableInfo, args);
            long startTime = System.currentTimeMillis();
            SqlResult sqlResult = executeSql(statementWrapper, sqlFragment, daoImplHashCode,cache);
            logger.debug("SQL EXECUTED | TIME: "+(System.currentTimeMillis() - startTime)+"ms.");
            flushCache(sqlResult, cache, context.getDirtyManager(), !context.getAutoCommit());
            return sqlResult.getResult();
        } catch (ExecutorException e) {
            logger.error(ErrorUtils.getSimpleExceptionLog(e));
            return errorResult;
        }
    }

    /**
     * 检查Jdbc环境。
     * @param jdbcContext
     * @param tableInfo
     * @return
     * @throws ExecutorException
     */
    protected Connection checkContext(JdbcContext jdbcContext, TableInfo tableInfo) throws ExecutorException {
        if (jdbcContext == null || tableInfo == null) {
            throw new ExecutorException("JdbcContext is null or tableInfo is null!");
        } else if (jdbcContext.isClosed()) {
            throw new ExecutorException("JdbcContext is closed!");
        } else {
            try {
                if (jdbcContext.getTransaction() == null) {
                    throw new ExecutorException("JdbcContext is not have Transaction!");
                } else {
                    Connection connection = jdbcContext.getTransaction().getConnection();
                    if (connection == null) {
                        throw new ExecutorException("Transaction cannot open Connection!");
                    } else
                        return connection;
                }
            } catch (SQLException e) {
                logger.error("The connection open failed\r\nmessage:" + e.getMessage() + "\r\ncause:" + e.getCause());
                throw new ExecutorException("Transaction cannot open Connection!");
            }
        }
    }

    /**
     * 检查方法参数。
     * @param args
     * @throws ExecutorException
     */
    protected abstract void checkArgs(Object[] args) throws ExecutorException;

    @Override
    protected void flushCache(SqlResult sqlResult, Cache cache, CacheDirtyManager dirtyManager, boolean isTransactional) throws ExecutorException {
        if (isTransactional && sqlResult != null && sqlResult.getCacheKey() != null && cache != null) {
            if (sqlResult.getFlag().equals(ExecuteCycle.FLUSH_FLAG)) {
                doFlushCache(sqlResult, cache, dirtyManager);
            } else {
                doClearCache(cache, dirtyManager);
            }
        }
    }

    /**
     * 更新缓存。
     * @param sqlResult
     * @param cache
     * @param dirtyManager
     */
    protected void doFlushCache(SqlResult sqlResult, Cache cache, CacheDirtyManager dirtyManager) {
        if (dirtyManager != null) {
            dirtyManager.get(cache).put(sqlResult.getCacheKey(), sqlResult.getResult());
        } else {
            cache.put(sqlResult.getCacheKey(), sqlResult.getResult());
        }
    }

    /**
     * 清理缓存。
     * @param cache
     * @param dirtyManager
     */
    protected void doClearCache(Cache<?, ?> cache, CacheDirtyManager dirtyManager) {
        if (dirtyManager != null) {
            dirtyManager.clear();
        } else {
            cache.clear();
        }
    }

    @Override
    protected StatementWrapper openStatement(SimpleSqlFragment sqlFragment, Connection connection, TableInfo tableInfo, Object[] args) throws ExecutorException {
        StatementWrapper statementWrapper;
        try {
            PreparedStatement statement = doOpenStatement(connection, tableInfo, sqlFragment.getNativeSql());
            statement.setFetchSize(tableInfo.getFetchSize());
            List<Object> params = sqlFragment.getParams();
            statementWrapper = new StatementWrapper(statement);
            statementWrapper.getMetaData().addProperty("tableInfo", tableInfo);
            doAfterOpenStatement(statementWrapper, params, args);
            CacheKey cacheKey=new CacheKey(tableInfo.getMappedClazz(), sqlFragment.getNativeSql(), null);
            cacheKey.addValueCollection(sqlFragment.getParams());
            statementWrapper.getMetaData().addProperty("cacheKey",cacheKey);
        } catch (SQLException e) {
            logger.error(ErrorUtils.getExceptionLog(e, sqlFragment.getNativeSql(), sqlFragment.getParams()));
            throw new ExecutorException("Statement open failed!");
        }
        return statementWrapper;
    }

    /**
     * 创建Statement对象，不同的使用场景将创建出不同的Statement。<br/>
     * 例如安全性考虑下将创建PrepareStatement以防止SQL注入。
     * @param connection
     * @param tableInfo
     * @param sql
     * @return
     * @throws SQLException
     */
    protected abstract PreparedStatement doOpenStatement(Connection connection,TableInfo tableInfo, String sql) throws SQLException;

    /**
     * 在Statement对象创建完成后，可以对prepareStatement进行设置值和修改值，对StatementWrapper添加数据，以便在执行SQL时能够使用到某些数据。
     * @param statementWrapper
     * @param params
     * @param args
     * @throws SQLException
     */
    protected abstract void doAfterOpenStatement(StatementWrapper statementWrapper, List<Object> params, Object[] args) throws SQLException;

    /**
     * 依据不同的模式执行不同的PreparedStatement的执行SQL操作
     * @param preparedStatement
     * @param mode 三种基础模式
     * @see StatementWrapper#BATCH
     * @see StatementWrapper#UPDATE
     * @see StatementWrapper#QUERY
     * @return
     * @throws SQLException
     * @throws ExecutorException
     */
    protected Object doExecuteSql(PreparedStatement preparedStatement, int mode) throws SQLException, ExecutorException {
        switch (mode) {
            case QUERY: return preparedStatement.executeQuery();
            case BATCH & UPDATE: return preparedStatement.executeBatch();
            case UPDATE:return preparedStatement.executeUpdate();
            default:throw new ExecutorException("Unsupported PreparedStatement Mode '"+mode+"'");
        }
    }
}

```

##### ExecuteCycle

声明了执行器的6个执行周期——参数检查阶段、连接打开阶段、Sql构建阶段、Statement实例化阶段、Sql执行阶段、缓存刷新阶段，开发者可以根据这六个阶段编程，对这些阶段进行增强，构建相对于独有业务逻辑效率更高的Executor及Template。

```java
package com.maxwellnie.velox.jpa.framework.proxy.executor.cycle;

import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.cahce.dirty.CacheDirtyManager;
import com.maxwellnie.velox.jpa.core.cahce.key.CacheKey;
import com.maxwellnie.velox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper;
import com.maxwellnie.velox.jpa.framework.exception.ExecutorException;
import com.maxwellnie.velox.jpa.framework.sql.SimpleSqlFragment;
import org.slf4j.Logger;

import java.sql.Connection;

/**
 * 执行器的执行周期
 *
 * @author Maxwell Nie
 * @since 1.1
 */
public abstract class ExecuteCycle {
    /**
     * 缓存清理标识。
     */
    public static final String CLEAR_FLAG = "1b4adf781a4ca21e";
    /**
     * 缓存更新标识。
     */
    public static final String FLUSH_FLAG = "3e5c6a74c1a9c3a1";
    protected Object errorResult = 0;

    protected abstract Logger getLogger();

    /**
     * 检查参数阶段。
     *
     * @param tableInfo
     * @param context
     * @param cache
     * @param daoImplHashCode
     * @param args
     * @throws ExecutorException
     */
    protected abstract void checkExecuteCondition(TableInfo tableInfo, JdbcContext context, Cache<Object, Object> cache, String daoImplHashCode, Object[] args) throws ExecutorException;

    /**
     * 获取连接阶段。
     *
     * @param jdbcContext
     * @return
     * @throws ExecutorException
     */
    protected abstract Connection doConnection(JdbcContext jdbcContext) throws ExecutorException;

    /**
     * 创建Sql阶段。
     *
     * @param args
     * @param tableInfo
     * @return
     * @throws ExecutorException
     */
    protected abstract SimpleSqlFragment getNativeSql(Object[] args, TableInfo tableInfo) throws ExecutorException;

    /**
     * 实例化Statement阶段。
     *
     * @param sqlFragment
     * @param connection
     * @param tableInfo
     * @param args
     * @return
     * @throws ExecutorException
     */
    protected abstract StatementWrapper openStatement(SimpleSqlFragment sqlFragment, Connection connection, TableInfo tableInfo, Object[] args) throws ExecutorException;

    /**
     * 执行Sql阶段。
     *
     * @param statementWrapper
     * @param sqlFragment
     * @param daoImplHashCode
     * @param cache
     * @return
     * @throws ExecutorException
     */
    protected abstract SqlResult executeSql(StatementWrapper statementWrapper, SimpleSqlFragment sqlFragment, String daoImplHashCode, Cache<Object, Object> cache) throws ExecutorException;

    /**
     * 刷新缓存阶段。
     *
     * @param sqlResult
     * @param cache
     * @param dirtyManager
     * @param isTransactional
     * @throws ExecutorException
     */
    protected abstract void flushCache(SqlResult sqlResult, Cache cache, CacheDirtyManager dirtyManager, boolean isTransactional) throws ExecutorException;

    /**
     * Sql执行的返回结果（包装器）。
     */
    public static class SqlResult {
        /**
         * 缓存工作标识。
         */
        private String flag;
        /**
         * sql执行结果。
         */
        private Object result;
        /**
         * 缓存的键。
         */
        private CacheKey cacheKey;

        public SqlResult() {
        }

        public SqlResult(String flag, Object result, CacheKey cacheKey) {
            this.flag = flag;
            this.result = result;
            this.cacheKey = cacheKey;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
        }

        public CacheKey getCacheKey() {
            return cacheKey;
        }

        public void setCacheKey(CacheKey cacheKey) {
            this.cacheKey = cacheKey;
        }
    }
}


```
##### BaseUpdateExecutor

```java
package com.maxwellnie.velox.jpa.framework.proxy.executor.update;

import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper;
import com.maxwellnie.velox.jpa.framework.exception.ExecutorException;
import com.maxwellnie.velox.jpa.framework.proxy.executor.BaseExecutor;
import com.maxwellnie.velox.jpa.framework.sql.SimpleSqlFragment;
import com.maxwellnie.velox.jpa.framework.sql.SqlBuilder;
import com.maxwellnie.velox.jpa.framework.utils.ErrorUtils;
import com.maxwellnie.velox.jpa.framework.utils.SqlUtils;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public abstract class BaseUpdateExecutor extends BaseExecutor {
    public BaseUpdateExecutor(Logger logger, Object errorResult) {
        super(logger, errorResult);
    }

    @Override
    protected SimpleSqlFragment getNativeSql(Object[] args, TableInfo tableInfo) throws ExecutorException {
        SimpleSqlFragment updateSql = new SimpleSqlFragment();
        List<ColumnInfo> columns = new LinkedList<>(tableInfo.getColumnMappedMap().values());
        doBuildUpdateSql(updateSql, columns, args, tableInfo);
        return updateSql;
    }

    @Override
    protected PreparedStatement doOpenStatement(Connection connection, TableInfo tableInfo, String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    @Override
    protected void doAfterOpenStatement(StatementWrapper statementWrapper, List<Object> params, Object[] args) throws SQLException {
        statementWrapper.setMode(StatementWrapper.UPDATE);
    }

    protected void doBuildUpdateSql(SimpleSqlFragment updateSql, List<ColumnInfo> columns, Object[] args, TableInfo tableInfo) {
        SqlBuilder<?> sqlBuilder = (SqlBuilder<?>) args[1];
        StringBuffer sqlStr = new StringBuffer("UPDATE ").append(tableInfo.getTableName()).append(" SET ");
        for (ColumnInfo columnInfo : columns) {
            sqlStr.append(columnInfo.getColumnName()).append("=?,");
            try {
                updateSql.addParam(columnInfo.getColumnMappedField().get(args[0]));
            } catch (IllegalAccessException e) {
                throw new ExecutorException(e);
            }
        }
        sqlStr.deleteCharAt(sqlStr.length() - 1).append(SqlUtils.buildSql(sqlBuilder, updateSql.getParams()));
        updateSql.setNativeSql(sqlStr.toString());
    }

    @Override
    protected SqlResult executeSql(StatementWrapper statementWrapper, SimpleSqlFragment sqlFragment, String daoImplHashCode, Cache<Object, Object> cache) throws ExecutorException {
        try (PreparedStatement preparedStatement = statementWrapper.getPrepareStatement()) {
            Object result = doExecuteSql(preparedStatement, statementWrapper.getMode());
            return new SqlResult(CLEAR_FLAG, result, null);
        } catch (SQLException e) {
            logger.error(ErrorUtils.getExceptionLog(e, sqlFragment.getNativeSql(), sqlFragment.getParams()));
            throw new ExecutorException("SQL error!");
        }
    }
}


```

##### BaseInsertExecutor

```java
package com.maxwellnie.velox.jpa.framework.proxy.executor.insert;

import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.primary.PrimaryKeyStrategy;
import com.maxwellnie.velox.jpa.core.jdbc.table.primary.generator.KeyGenerator;
import com.maxwellnie.velox.jpa.core.jdbc.table.primary.generator.NoKeyGenerator;
import com.maxwellnie.velox.jpa.core.jdbc.table.primary.keyselector.JdbcSelector;
import com.maxwellnie.velox.jpa.core.jdbc.table.primary.keyselector.KeySelector;
import com.maxwellnie.velox.jpa.core.jdbc.table.primary.keyselector.NoKeySelector;
import com.maxwellnie.velox.jpa.core.manager.KeyStrategyManager;
import com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper;
import com.maxwellnie.velox.jpa.framework.exception.ExecutorException;
import com.maxwellnie.velox.jpa.framework.proxy.executor.BaseExecutor;
import com.maxwellnie.velox.jpa.framework.sql.SimpleSqlFragment;
import com.maxwellnie.velox.jpa.framework.utils.ErrorUtils;
import com.maxwellnie.velox.jpa.framework.utils.ExecutorUtils;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public abstract class BaseInsertExecutor extends BaseExecutor {
    public BaseInsertExecutor(Logger logger, Object errorResult) {
        super(logger, errorResult);
    }

    @Override
    protected SimpleSqlFragment getNativeSql(Object[] args, TableInfo tableInfo) throws ExecutorException {
        SimpleSqlFragment insertSql = new SimpleSqlFragment();
        List<ColumnInfo> columns = new LinkedList<>();
        if (!tableInfo.hasPk())
            columns.add(tableInfo.getPkColumn());
        columns.addAll(tableInfo.getColumnMappedMap().values());
        doBuildInsertSql(insertSql, columns, args, tableInfo);
        return insertSql;
    }

    /**
     * 构建Sql语句
     *
     * @param insertSql
     * @param columns
     * @param args
     * @param tableInfo
     * @throws ExecutorException
     */
    protected void doBuildInsertSql(SimpleSqlFragment insertSql, List<ColumnInfo> columns, Object[] args, TableInfo tableInfo) throws ExecutorException {
        StringBuffer insertStr = new StringBuffer("INSERT INTO ")
                .append(tableInfo.getTableName()).append(" (");
        for (ColumnInfo columnInfo : columns) {
            insertStr.append(columnInfo.getColumnName()).append(",");
        }
        insertStr.deleteCharAt(insertStr.length() - 1).append(")")
                .append(" VALUES(");
        for (ColumnInfo columnInfo : columns) {
            insertStr.append("?").append(",");
        }
        insertSql.setNativeSql(insertStr.deleteCharAt(insertStr.length() - 1).append(");").toString());
    }

    @Override
    protected PreparedStatement doOpenStatement(Connection connection, TableInfo tableInfo, String sql) throws SQLException {
        if (KeyStrategyManager.getPrimaryKeyStrategy(tableInfo.getPkColumn().getStrategyName()).getKeySelector() instanceof JdbcSelector)
            return connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        else
            return connection.prepareStatement(sql);
    }

    @Override
    protected SqlResult executeSql(StatementWrapper statementWrapper, SimpleSqlFragment sqlFragment, String daoImplHashCode, Cache<Object, Object> cache) throws ExecutorException {
        PrimaryKeyStrategy keyStrategy = ExecutorUtils.of(statementWrapper, "keyStrategy");
        TableInfo tableInfo = ExecutorUtils.of(statementWrapper, "tableInfo");
        Object[] entityInstances = ExecutorUtils.of(statementWrapper, "entityInstances");
        try (PreparedStatement preparedStatement = statementWrapper.getPrepareStatement()) {
            Object result = doExecuteSql(preparedStatement, statementWrapper.getMode());
            setPrimaryKeyFormSelectedKey(keyStrategy, preparedStatement, result, entityInstances, tableInfo);
            return new SqlResult(CLEAR_FLAG, result, null);
        } catch (SQLException | IllegalAccessException e) {
            logger.error(ErrorUtils.getExceptionLog(e, sqlFragment.getNativeSql(), sqlFragment.getParams()));
            throw new ExecutorException("SQL error!");
        }
    }

    @Override
    protected void doAfterOpenStatement(StatementWrapper statementWrapper, List<Object> params, Object[] args) throws SQLException {
        try {
            TableInfo tableInfo = ExecutorUtils.of(statementWrapper, "tableInfo");
            PrimaryKeyStrategy keyStrategy = ExecutorUtils.of(statementWrapper, "keyStrategy");
            Object[] entityInstances = ExecutorUtils.of(statementWrapper, "entityInstances");
            setPrimaryKeyFromGeneratedKey(keyStrategy, entityInstances, tableInfo);
            statementWrapper.setMode(StatementWrapper.UPDATE);
        } catch (IllegalAccessException e) {
            throw new ExecutorException(e);
        }
    }

    protected void setPrimaryKeyFromGeneratedKey(PrimaryKeyStrategy keyStrategy, Object[] entityInstances, TableInfo tableInfo) throws IllegalAccessException {
        if (tableInfo.hasPk()) {
            KeyGenerator keyGenerator = keyStrategy.getKeyGenerator();
            if (!(keyGenerator instanceof NoKeyGenerator)) {
                for (Object entityInstance : entityInstances) {
                    tableInfo.getPkColumn().getColumnMappedField().set(entityInstance, keyGenerator.nextKey());
                }
            }
        }
    }

    protected void setPrimaryKeyFormSelectedKey(PrimaryKeyStrategy keyStrategy, PreparedStatement preparedStatement, Object result, Object[] entityInstances, TableInfo tableInfo) throws IllegalAccessException {
        if (tableInfo.hasPk()) {
            KeySelector keySelector = keyStrategy.getKeySelector();
            if (keySelector instanceof NoKeySelector)
                return;
            Object primaryKeys = keySelector.selectGeneratorKey(preparedStatement, result);
            if (primaryKeys != null) {
                if (primaryKeys instanceof Object[]) {
                    Object[] objects = (Object[]) primaryKeys;
                    if (objects.length != 0 && objects.length == entityInstances.length)
                        for (int index = 0; index < entityInstances.length; index++)
                            tableInfo.getPkColumn().getColumnMappedField().set(entityInstances[index], objects[index]);
                }
            }
        }
    }

}


```

##### BaseDeleteExecutor

```java
package com.maxwellnie.velox.jpa.framework.proxy.executor.delete;

import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper;
import com.maxwellnie.velox.jpa.framework.exception.ExecutorException;
import com.maxwellnie.velox.jpa.framework.proxy.executor.BaseExecutor;
import com.maxwellnie.velox.jpa.framework.sql.SimpleSqlFragment;
import com.maxwellnie.velox.jpa.framework.utils.ErrorUtils;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Maxwell Nie
 */
public abstract class BaseDeleteExecutor extends BaseExecutor {
    public BaseDeleteExecutor(Logger logger, Object errorResult) {
        super(logger, errorResult);
    }

    @Override
    protected SimpleSqlFragment getNativeSql(Object[] args, TableInfo tableInfo) throws ExecutorException {
        SimpleSqlFragment deleteSql = new SimpleSqlFragment();
        doBuildDeleteSql(deleteSql, null, args, tableInfo);
        return deleteSql;
    }

    protected void doBuildDeleteSql(SimpleSqlFragment deleteSql, List<ColumnInfo> columns, Object[] args, TableInfo tableInfo) {
        StringBuffer sqlStr = new StringBuffer("DELETE ")
                .append(" FROM ").append(tableInfo.getTableName());
        deleteSql.setNativeSql(sqlStr.toString());
    }

    @Override
    protected PreparedStatement doOpenStatement(Connection connection, TableInfo tableInfo, String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    @Override
    protected void doAfterOpenStatement(StatementWrapper statementWrapper, List<Object> params, Object[] args) throws SQLException {
        statementWrapper.setMode(StatementWrapper.UPDATE);
    }

    @Override
    protected SqlResult executeSql(StatementWrapper statementWrapper, SimpleSqlFragment sqlFragment, String daoImplHashCode, Cache<Object, Object> cache) throws ExecutorException {
        try (PreparedStatement preparedStatement = statementWrapper.getPrepareStatement()) {
            Object result = doExecuteSql(preparedStatement, statementWrapper.getMode());
            return new SqlResult(CLEAR_FLAG, result, null);
        } catch (SQLException e) {
            logger.error(ErrorUtils.getExceptionLog(e, sqlFragment.getNativeSql(), sqlFragment.getParams()));
            throw new ExecutorException("SQL error!");
        }
    }
}


```

##### BaseQueryExecutor


```java
package com.maxwellnie.velox.jpa.framework.proxy.executor.query;

import com.maxwellnie.velox.jpa.core.cahce.Cache;
import com.maxwellnie.velox.jpa.core.cahce.key.CacheKey;
import com.maxwellnie.velox.jpa.core.jdbc.table.TableInfo;
import com.maxwellnie.velox.jpa.core.jdbc.table.column.ColumnInfo;
import com.maxwellnie.velox.jpa.core.proxy.executor.wrapper.StatementWrapper;
import com.maxwellnie.velox.jpa.core.utils.jdbc.ResultSetUtils;
import com.maxwellnie.velox.jpa.framework.exception.ExecutorException;
import com.maxwellnie.velox.jpa.framework.proxy.executor.BaseExecutor;
import com.maxwellnie.velox.jpa.framework.sql.SimpleSqlFragment;
import com.maxwellnie.velox.jpa.framework.utils.ErrorUtils;
import com.maxwellnie.velox.jpa.framework.utils.ExecutorUtils;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * 基本的查询执行器
 *
 * @author Maxwell Nie
 */
public abstract class BaseQueryExecutor extends BaseExecutor {
    public BaseQueryExecutor(Logger logger, Object errorResult) {
        super(logger, errorResult);
    }

    @Override
    protected SimpleSqlFragment getNativeSql(Object[] args, TableInfo tableInfo) throws ExecutorException {
        SimpleSqlFragment selectSql = new SimpleSqlFragment();
        List<ColumnInfo> columns = new LinkedList<>();
        if (tableInfo.hasPk())
            columns.add(tableInfo.getPkColumn());
        columns.addAll(tableInfo.getColumnMappedMap().values());
        doBuildSelectSql(selectSql, columns, args, tableInfo);
        return selectSql;
    }

    protected void doBuildSelectSql(SimpleSqlFragment sqlFragment, List<ColumnInfo> columns, Object[] args, TableInfo tableInfo) {
        StringBuffer sqlStr = new StringBuffer("SELECT ");
        for (ColumnInfo columnInfo : columns) {
            sqlStr.append(columnInfo.getColumnName()).append(",");
        }
        sqlStr.deleteCharAt(sqlStr.length() - 1).append(" FROM ").append(tableInfo.getTableName());
        sqlFragment.setNativeSql(sqlStr);
    }

    @Override
    protected PreparedStatement doOpenStatement(Connection connection, TableInfo tableInfo, String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    @Override
    protected void doAfterOpenStatement(StatementWrapper statementWrapper, List<Object> params, Object[] args) throws SQLException {
        statementWrapper.setMode(StatementWrapper.QUERY);
    }

    @Override
    protected SqlResult executeSql(StatementWrapper statementWrapper, SimpleSqlFragment sqlFragment, String daoImplHashCode, Cache<Object, Object> cache) throws ExecutorException {
        TableInfo tableInfo = ExecutorUtils.of(statementWrapper, "tableInfo");
        CacheKey cacheKey = ExecutorUtils.of(statementWrapper, "cacheKey");
        cacheKey.setDaoImplHashCode(daoImplHashCode);
        try (PreparedStatement preparedStatement = statementWrapper.getPrepareStatement()) {
            List result = (List) cache.get(cacheKey);
            if (result == null) {
                ResultSet resultSet = preparedStatement.executeQuery();
                result = ResultSetUtils.convertEntity(resultSet, tableInfo);
                resultSet.close();
            } else
                logger.debug("Cache Hit.");
            return new SqlResult(FLUSH_FLAG, result, cacheKey);
        } catch (SQLException e) {
            logger.error(ErrorUtils.getExceptionLog(e, sqlFragment.getNativeSql(), sqlFragment.getParams()));
            throw new ExecutorException("SQL error!");
        }
    }
}

```
