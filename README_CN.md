# VeloxSql

### 什么是VeloxSql？

VeloxSql是一款基于Java的ORM框架，扩展性很强，可以使用少量SQL语句、少量代码实现crud操作。

### 适配性

Java8+ & spring 5+，需要实现方言接口适配数据库。

### 示例

Gitee仓库：<a href="https://gitee.com/maxwellnie/velox-sql-demo.git">示例代码<a/><br/>
Github仓库：<a href="https://github.com/maxwellnie/velox-sql-demo.git">示例代码<a/>

### Api文档

Gitee仓库：<a href="https://gitee.com/maxwellnie/velox-sql-demo.git/apidocs/index.html">示例代码<a/><br/>
Github仓库：<a href="https://github.com/maxwellnie/velox-sql-demo.git/apidocs/index.html">示例代码<a/>

### 使用教程

pom.xml添加依赖
```
<dependencies>
    <dependency>
        <groupId>io.github.maxwellnie</groupId>
        <artifactId>velox-sql-spring-boot-starter</artifactId>
        <version>1.1.1</version>
    </dependency>
    <dependency>
        <groupId>net.bytebuddy</groupId>
        <artifactId>byte-buddy</artifactId>
        <version>1.14.5</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>1.6.1</version>
    </dependency>
</dependencies>
```
配置文件application.yml添加velox-sql配置
```
velox-sql:
  global:
    table-prefix: tb_
    stand-table : true
    stand-column: true
    cache: true
    is-task-queue: true
```
编写实体类代码：
```
@Getter
@Setter
@Entity("tb_user")
public class User extends Base implements Serializable {
    public User() {
    }

    public User(int userId, String loginName, String password) {
        this.userId = userId;
        this.loginName = loginName;
        this.password = password;
    }

    @PrimaryKey(strategyKey = KeyStrategyManager.JDBC_AUTO, convertor = IntegerConvertor.class)
    private int userId;
    /**
     * 不排除这个数据库中没有的字段将报错。
     */
    @Column(isExclusion = true)
    private Object voidColumn;
    private String loginName;
    private String password;


    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", loginName="+ getLoginName() +
                ", password=" + getPassword() +
                '}';
    }
}
```
启动类设置扫描包：
```
@SpringBootApplication
@DaoImplConf(value = "com.example.ttdemo.po")
public class TtdemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(TtdemoApplication.class, args);
    }

}
```
直接使用创建好的代理Dao对象：
```
@Controller("/")
public class UserController {
    @Resource
    private BaseDao<User> userDao;
    @GetMapping("/test")
    public void test(HttpServletResponse response) throws IOException {
    System.out.println(userDao);
    response.getWriter().println(userDao.select(null).size());
    }
}
```
测试用例：
```
@SpringBootTest
class TtdemoApplicationTests {
    @Resource
    BaseDao<User> userBaseDao;
    @org.junit.jupiter.api.Test
    void test(){
        /**
         * 测试查询
         */
        System.err.println("查询结果:"+userBaseDao.select(null).size());
        /**
         * 测试分页查询
         */
        System.err.println("分页结果:"+userBaseDao.selectPage(null, null).getResult());
        /**
         * 测试插入
         */
        User user = new User();
        user.setLoginName("maxwell");
        user.setPassword("123456");
        System.err.println("添加结果:"+userBaseDao.insert(user));
        SqlDecorator<User> sqlDecorator = new SqlDecorator<User>().where().eq("user_id", user.getUserId()).build();
        System.err.println("该条目:"+userBaseDao.select(sqlDecorator));
        /**
         * 测试更新
         */
        user.setLoginName("????sdjks");
        System.err.println("更新结果:"+userBaseDao.update(user, sqlDecorator));
        System.err.println("被更新条目:"+userBaseDao.select(sqlDecorator));
        /**
         * 测试删除
         */
        System.err.println("删除结果:"+userBaseDao.delete(sqlDecorator));
        System.err.println("该条目:"+userBaseDao.select(sqlDecorator));
        /**
         * 测试查询条目
         */
        System.err.println("总数据量:"+userBaseDao.count(null));

    }
}
```