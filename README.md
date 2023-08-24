# CrazySQL

#### 介绍
基于Java的数据持久化框架

#### 软件架构
软件架构说明

#### 使用说明
springboot:
导入依赖
```
<dependencies>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.8</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>io.github.akibanoichiichiyoha</groupId>
            <artifactId>CrazySQL-boot-starter</artifactId>
            <version>1.4.6</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>javax.persistence</groupId>
            <artifactId>persistence-api</artifactId>
            <version>1.0.2</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
    </dependencies>
```
配置文件：
```
spring:
  datasource:
    type: com.crazy.sql.spring.boot.datasource.CrazySQLDataSource
    url: jdbc:mysql://localhost:3307/bounddatabase?useUnicode=true&characterEncoding=utf8
    username: root
    password: 123456
    driver-class-name: com.mysql.jdbc.Driver
crazy-sql:
  table-suffix: tb_
  maximum: 50
  stand-column: true
```
table-suffix:表名前缀：tb_user中的tb_
maximum:最大连接数量
stand-column:userId->user_id
你需要在你的配置类中注册AccessorBean:
```
@Configuration
public class MyCrazySQLConfig {
    @Bean
    public CacheManager cacheManager(){
        return new SimpleCacheManager();
    }
    @Bean
    public Accessor<User> userAccessor(AccessorFactory accessorFactory){
        return new AccessorSession<>(accessorFactory.produce(User.class));
    }
    @Bean
    public Accessor<Bound> boundAccessor(AccessorFactory accessorFactory){
        return new AccessorSession<>(accessorFactory.produce(Bound.class));
    }
}
```
如果不想使用spring，也可以使用如下的方法：
```
    <dependencies>
        <dependency>
            <groupId>io.github.akibanoichiichiyoha</groupId>
            <artifactId>CrazySQL-core</artifactId>
            <version>1.4.6</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.8</version>
        </dependency>
        <dependency>
            <groupId>javax.persistence</groupId>
            <artifactId>persistence-api</artifactId>
            <version>1.0.2</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.6.1</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>1.6.1</version>
        </dependency>
    </dependencies>
```
使用如下代码即可
```
public class Main {
    public static void main(String[] args) throws SQLException {
        CrazySQLConfig config=CrazySQLConfig.getInstance();
        CrazySQLConfig.getInstance().setMaximum(20);
        CrazySQLConfig.getInstance().setTableSuffix("tb_");
        config.setProperties(properties);
        CrazySQLConfig.getInstance().setStandColumn(true);
        Class.forName("com.mysql.jdbc.Driver");
        SimpleConnectionPool simpleConnectionPool=new SimpleConnectionPool();
        simpleConnectionPool.setDriverClassName("com.mysql.jdbc.Driver");
        simpleConnectionPool.setUrl("jdbc:mysql://localhost:3307/bounddatabase");
        simpleConnectionPool.setUsername("root");
        simpleConnectionPool.setPassword("123456");
        System.out.println(simpleConnectionPool.size());
        AccessorFactory factory=new StandAccessorFactory(simpleConnectionPool,null,false);
        Accessor<User> accessor=factory.produce(User.class);
        System.out.println(accessor.queryAll());
        System.out.println(accessor.queryAll());
    }
}
```