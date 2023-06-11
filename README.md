# CrazySQL

#### 介绍
基于Java的数据持久化框架

#### 软件架构
软件架构说明

#### 使用说明

首先，引入CrazySQL的依赖和Mysql连接驱动，以及日志依赖
```
    <dependencies>
        <dependency>
          <groupId>io.github.akibanoichiichiyoha</groupId>
          <artifactId>CrazySQL</artifactId>
          <version>1.3</version>
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
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>1.6.1</version>
        </dependency>
    </dependencies>
```
接着，这里有一个代码实例：
```
import com.crazy.sql.core.Accessor;
import com.crazy.sql.core.cahce.manager.impl.SimpleCacheManager;
import com.crazy.sql.core.config.CrazySQLConfig;
import com.crazy.sql.core.enums.QueryCondition;
import com.crazy.sql.core.factory.impl.SimpleConnectionPoolFactory;
import com.crazy.sql.core.factory.impl.StandAccessorFactory;
import com.crazy.sql.core.query.QueryWord;
import com.crazy.sql.core.utils.SQLUtils;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws SQLException {
        Properties properties = new Properties();
        properties.put("driverClassName", "com.mysql.jdbc.Driver");
        properties.put("url", "jdbc:mysql://localhost:3307/test");
        properties.put("userName", "root");
        properties.put("password", "123456");
        properties.put("maximum", "100");
        SQLConfig.setConfig(properties);
        CrazySQL<User> crazySQL = new StandCrazySQLFactory(
                new SimpleConnectionPoolFactory(),
                new SimpleCacheManager(),
                true)
                .produce(new SQLUtils<>(User.class, "tb_", true));
        crazySQL.insert(new User(1,"jij",new Date()));
        long time = System.currentTimeMillis();
        System.out.println("添加结果:" + crazySQL.insert(new User(1010,"nihao",new Date())));
        System.out.println("耗时：" + (System.currentTimeMillis() - time) + "ms");
        time = System.currentTimeMillis();
        System.out.println("修改结果:" + crazySQL.update(new User(1010,"nihao",new Date())));
        System.out.println("耗时：" + (System.currentTimeMillis() - time) + "ms");
        time = System.currentTimeMillis();
        System.out.println("查询全部结果:" + crazySQL.queryAll());
        System.out.println("耗时：" + (System.currentTimeMillis() - time) + "ms");
        time = System.currentTimeMillis();
        System.out.println("查询一条数据结果:" + crazySQL.queryOne(new User(3,null,null)));
        System.out.println("耗时：" + (System.currentTimeMillis() - time) + "ms");
        time = System.currentTimeMillis();
        System.out.println("条件查询结果:" + crazySQL.queryByWords(new QueryWord("userId",QueryCondition.IE,"3")));
        System.out.println("耗时：" + (System.currentTimeMillis() - time) + "ms");
        time = System.currentTimeMillis();
        System.out.println("查询缓存全部结果:" + crazySQL.queryAll());
        System.out.println("耗时：" + (System.currentTimeMillis() - time) + "ms");
        time = System.currentTimeMillis();
        System.out.println("查询缓存一条数据结果:" + crazySQL.queryOne(new User(3,null,null)));
        System.out.println("耗时：" + (System.currentTimeMillis() - time) + "ms");
        crazySQL.getCacheManager().destroy();
    }
}
```
