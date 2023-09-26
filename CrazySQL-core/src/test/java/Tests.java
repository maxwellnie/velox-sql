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
        SimpleConnectionPool simpleConnectionPool = new SimpleConnectionPool();
        simpleConnectionPool.setDriverClassName("com.mysql.jdbc.Driver");
        simpleConnectionPool.setUsername("root");
        simpleConnectionPool.setPassword("123456");
        simpleConnectionPool.setUrl("jdbc:mysql://localhost:3307/bounddatabase");
        GlobalConfig globalConfig=GlobalConfig.getInstance();
        globalConfig.setCache(true);
        globalConfig.setClazzArr(new Class[]{User.class});
        globalConfig.setTablePrefix("tb_");
        globalConfig.setStandColumn(true);
        globalConfig.setStandTable(true);
        Environment environment=new Environment(new JdbcTransactionFactory(),simpleConnectionPool);
        JdbcContextFactory jdbcContextFactory=new SimpleContextFactory(environment);
        JdbcContext jdbcContext= jdbcContextFactory.produce();
        Accessor<User> accessor= (Accessor<User>) environment.getAccessor(User.class).produce(jdbcContext);
        accessor.queryAll(new SqlBuilder<User>().where().eq("user_id",32).build());
        Accessor<User> accessor1= (Accessor<User>) environment.getAccessor(User.class).produce(jdbcContext);
        long start=System.currentTimeMillis();
        List<User> list=accessor1.queryAll(new SqlBuilder<User>().where().like("user_id",66, LikeFragment.ALL).build());
        System.out.println(list);
        System.out.println(System.currentTimeMillis()-start);
    }
}


