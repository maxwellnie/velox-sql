# CrazySQL

#### 介绍
基于Java的数据持久化框架

#### 软件架构
软件架构说明

#### 使用说明
导入依赖
```
    <dependencies>
        <dependency>
            <groupId>io.github.akibanoichiichiyoha</groupId>
            <artifactId>CrazySQL-core</artifactId>
            <version>1.5.0</version>
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
example:
```
import com.crazy.sql.core.accessor.SqlBuilder;
import com.crazy.sql.core.accessor.Accessor;
import com.crazy.sql.core.accessor.env.Environment;
import com.crazy.sql.core.cahce.impl.SimpleCache;
import com.crazy.sql.core.config.GlobalConfig;
import com.crazy.sql.core.jdbc.context.JdbcContext;
import com.crazy.sql.core.jdbc.context.JdbcContextFactory;
import com.crazy.sql.core.jdbc.context.SimpleContext;
import com.crazy.sql.core.jdbc.context.SimpleContextFactory;
import com.crazy.sql.core.jdbc.pool.impl.SimpleConnectionPool;
import com.crazy.sql.core.jdbc.sql.condition.LikeFragment;
import com.crazy.sql.core.proxy.AccessorInvokeHandler;
import com.crazy.sql.core.jdbc.transaction.Transaction;
import com.crazy.sql.core.jdbc.transaction.impl.jdbc.JdbcTransactionFactory;
import com.crazy.sql.core.utils.reflect.ReflectUtils;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class Tests {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, InterruptedException {
        //配置连接池
        SimpleConnectionPool simpleConnectionPool = new SimpleConnectionPool();
        simpleConnectionPool.setDriverClassName("com.mysql.jdbc.Driver");
        simpleConnectionPool.setUsername("root");
        simpleConnectionPool.setPassword("123456");
        simpleConnectionPool.setUrl("jdbc:mysql://localhost:3307/bounddatabase");
        //获取全局配置实例
        GlobalConfig globalConfig=GlobalConfig.getInstance();
        //开启缓存
        globalConfig.setCache(true);
        //设置实体类class对象数组，用于获取Accessor实例
        globalConfig.setClazzArr(new Class[]{User.class});
        //设置表前缀
        globalConfig.setTablePrefix("tb_");
        //开启名称转化：SpringBoot -> spring_boot
        globalConfig.setStandColumn(true);
        globalConfig.setStandTable(true);
        //创建框架环境
        Environment environment=new Environment(new JdbcTransactionFactory(),simpleConnectionPool);
        //创建Jdbc环境工厂
        JdbcContextFactory jdbcContextFactory=new SimpleContextFactory(environment);
        //生成实例
        JdbcContext jdbcContext= jdbcContextFactory.produce();
        //获取实体类对应Accessor实例
        Accessor<User> accessor= (Accessor<User>) environment.getAccessor(User.class).produce(jdbcContext);
        //使用查询构建工具构建查询操作
        List<User> list=accessor.queryAll(new SqlBuilder<User>().where().like("user_id",66, LikeFragment.ALL).build());
        System.out.println(list);
    }
}
```
```
import com.crazy.sql.core.annotation.Table;
import com.crazy.sql.core.annotation.TableField;
import com.crazy.sql.core.annotation.TableId;
import com.crazy.sql.core.enums.PrimaryMode;

import java.util.Date;

/**
 * @author Akiba no ichiichiyoha
 */
@Table("tb_user")
public  class User {
    @TableId(PrimaryMode.JDBC_AUTO)
    public int userId;
    public String loginName;
    public String password;
    private String userName;
    private String roleName;
    private String rights;
    private String iconPath;
    private boolean sex;
    @TableField(value = "last_time")
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