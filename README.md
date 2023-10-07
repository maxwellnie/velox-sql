# VeloxJPA

#### 介绍
基于Java的数据持久化框架，目前只适配了MySql数据库和Java8

#### 软件重要部分
1. Executor - 代理Dao的方法。
2. TypeConvertor - 处理Jdbc提供数据类型到期望的Java数据类型。
3. KeyStrategy - 这个实体包括主键生成器和主键查询器，前者用于产生主键值，后者用于查询产生的主键值。
4. JdbcContext - 这个实体包含了事务实例、缓存实例和数据库连接实例。
5. Transaction - JdbcContext事务的实体。
6. MetaObject - 元对象，可以参考QT和mybatis中的元对象。

#### 注意事项

由于 0.2.original 及以前的版本含有重大安全漏洞，进行了下架处理。

#### 使用说明
导入依赖
```
    <dependencies>
        <dependency>
            <groupId>io.github.maxwellnie</groupId>
            <artifactId>velox-jpa-core</artifactId>
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
快速上手:
```
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
        veloxJpaConfig.setCacheClass(LRUCache.class);
        veloxJpaConfig.setClazzArr(new Class[]{User.class});
        veloxJpaConfig.setTablePrefix("tb_");
        veloxJpaConfig.setStandColumn(true);
        veloxJpaConfig.setStandTable(true);
        veloxJpaConfig.setDaoImplClazz(DaoImpl.class);
        //初始化JdbcContext生产环境环境
        Environment environment=new Environment(new JdbcTransactionFactory(),simpleConnectionPool, veloxJpaConfig);
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
```
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
    @PrimaryKey(PrimaryMode.JDBC_AUTO)
    public int userId;
    public String loginName;
    public String password;
    private String userName;
    private String roleName;
    private String rights;
    private String iconPath;
    private boolean sex;
    @Column(value = "last_time")
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
sql:
```
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
) ENGINE = InnoDB AUTO_INCREMENT = 3060885 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
```