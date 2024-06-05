# VeloxSql

### What is VeloxSql？

VeloxSql is a Java-based ORM framework with strong extensibility, which can use a small number of SQL statements and a small amount of code to achieve crud operations.

### Suitability

Java8+ & spring 5+. The dialect interface adaptation database needs to be implemented.

### Example

Gitee：<a href="https://gitee.com/maxwellnie/velox-sql-demo.git">example<a/><br/>
Github：<a href="https://github.com/maxwellnie/velox-sql-demo.git">example<a/>

### Tutorials

pom.xml Add dependencies
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
In application.yml you must add velox-sql configuration
```
velox-sql:
  global:
    table-prefix: tb_
    stand-table : true
    stand-column: true
    cache: true
    is-task-queue: true
```
Entity：
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
     * If you don't want to use the column, you can set it to exclusion
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
Set entities package path：
```
@SpringBootApplication
@DaoImplConf(value = "com.example.demo.po")
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
```
Use proxy object to access the database：
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
Test case：
```
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
         * Test page
         */
        System.err.println("page result:"+userBaseDao.selectPage(null, null).getResult());
        /**
         * Test insert
         */
        User user = new User();
        user.setLoginName("maxwell");
        user.setPassword("123456");
        System.err.println("insert result:"+userBaseDao.insert(user));
        SqlDecorator<User> sqlDecorator = new SqlDecorator<User>().where().eq("user_id", user.getUserId()).build();
        System.err.println("added entity:"+userBaseDao.select(sqlDecorator));
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