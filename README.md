# VeloxJPA

### 什么是VeloxJPA？

VeloxJPA是一款基于Java的，符合JPA规范的数据持久化框架，扩展性很强，可以根据自身需要，自定义或者是优化SQL来生产出符合业务场景的VeloxJPATemplate（数据持久化抽象模板）。
### 适配性

目前只适配了mysql5+ & Java8+ & spring 5+

### 安全漏洞警告

由于 0.2.original 及以前的版本含有重大安全漏洞，进行了下架处理。

### 使用文档

#### java原生快速上手:
导入依赖：
```xml
<dependencies>
    <dependency>
        <groupId>io.github.maxwellnie</groupId>
        <artifactId>velox-jpa-core</artifactId>
        <version>1.0</version>
    </dependency>
      <dependency>
        <groupId>io.github.maxwellnie</groupId>
        <artifactId>velox-jpa-core-template</artifactId>
        <version>1.0</version>
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
import com.maxwellnie.vleox.jpa.core.dao.support.DaoImpl;
import com.maxwellnie.vleox.jpa.core.dao.support.SqlBuilder;
import com.maxwellnie.vleox.jpa.core.dao.support.env.Environment;
import com.maxwellnie.vleox.jpa.core.cahce.impl.LRUCache;
import com.maxwellnie.vleox.jpa.core.config.simple.VeloxJpaConfig;
import com.maxwellnie.vleox.jpa.core.jdbc.context.JdbcContext;
import com.maxwellnie.vleox.jpa.core.jdbc.context.JdbcContextFactory;
import com.maxwellnie.vleox.jpa.core.jdbc.context.SimpleContextFactory;
import com.maxwellnie.vleox.jpa.core.jdbc.pool.impl.SimpleConnectionPool;
import com.maxwellnie.vleox.jpa.core.jdbc.transaction.impl.jdbc.JdbcTransactionFactory;

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
     	//注册实体User的DaoImplFactory
     	enviroment.addDaoImpl(User.class);
        //JdbcContext工厂
        JdbcContextFactory jdbcContextFactory=new SimpleContextFactory(environment);
        JdbcContext jdbcContext= jdbcContextFactory.produce(true);
        //获取实例
        DaoImpl<User> daoImpl = (DaoImpl<User>) environment.getDaoImplFactory(User.class).produce(jdbcContext);
        //查询数据
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
                <version>1.0</version>
            </dependency>
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>druid</artifactId>
                <version>1.2.16</version>
            </dependency>
        <dependency>
            <groupId>io.github.maxwellnie</groupId>
            <artifactId>velox-jpa-core-template</artifactId>
            <version>1.0</version>
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

接下来是四大管理器：ConvertorManager、KeyStrategyManager、MethodMappedManager、TransactionManager。

##### ConvertorManager 

TypeConvertor的管理器，可以注册、或者获取对应Java数据类型的转换器，用于查询数据库数据后转换从JDBC API获取到的数据。例如将java.sql.Date转换为java.util.Date

##### KeyStrategyManager 

 KeyStrategy的管理器，可以注册、或者获取对应名字的KeyStrategy，用于添加数据时获取主键值，和返回主键值。

##### MethodMappedManager 

Executor和被注册方法的映射管理，最好不要在运行时注册映射到这个管理器，可能会产生未知的后果。

##### TransactionManager 

TransactionFactory的管理器，可以注册、或者获取对应名字的TransactionFactory，用于赋予操作数据库时JdbcContext的事务。可以获取到的值有JDBC_AUTO，即将被废弃，以后的版本可能就见不到它了，最好不用使用。

##### TypeConvertor<T> 类型转换器

当JDBC从数据库取出数据后，会调用ResultSet的getObject方法获取数据，这个转换器的意义就在于可以将JDBC API 提供的对象转换为我们想要的对象，如果结合@Column注解，这种方式将会类似于ORM。

```java
/**
 * @author Maxwell Nie
 */
public interface TypeConvertor<T> {
    public T convert(Object original);
}

例如，我们需要将JDBC API查询出的Date转换，从类型java.sql.Date,java.sql.TimeStamp转换为java.util.Date，我们可以编写DateConvertor：
/**
 * @author Maxwell Nie
 */
public class DateConvertor implements TypeConvertor<Date> {
    @Override
    public Date convert(Object original) {
        if (original == null)
            return null;
        else if (original instanceof Time){
            throw new TypeNotEqualsException("You want to get type of java.util.Date,but "+original+" is type of Time");
        }else if(original instanceof Timestamp){
            return new Date(((Timestamp) original).getTime());
        }else if(original instanceof java.sql.Date){
            return new Date(((java.sql.Date) original).getTime());
        }else 
            throw new TypeNotEqualsException("You want to get type of java.util.Date,but "+original+" is "+original.getClass().getName());
    }
}
```

##### Generator主键值生成器

用于在添加前生成主键值。

```java
例如没有生成器的实例
public class NoGenerator implements Generator {
	private long seed=1; 
    @Override
    public Object nextKey() {
        return nextKey(192939293L);
    }
  	public long nextKey(long time){
    	return time * seed;
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

