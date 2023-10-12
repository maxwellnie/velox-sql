package com.maxwellnie.velox.jpa.core.utils.reflect;

/**
 * @author Maxwell Nie
 */
public abstract class ReflectUtils {
    /**
     * <pre class="code">
     *  !test unit
     *   public static void main(String[] args) {
     *        &#064;Entity
     *        class User{
     *            &#064;PrimaryKey(value  = "user_id",primaryMode =PrimaryMode.OTHER,strategyKey = "other")
     *            long userId;
     *            String password;
     *        }
     *        System.out.println(getTableInfo(User.class));
     *    }
     * </pre>
     **/
    public static String getClassName(Class<?> clazz) {
        return clazz.getName();
    }
}
