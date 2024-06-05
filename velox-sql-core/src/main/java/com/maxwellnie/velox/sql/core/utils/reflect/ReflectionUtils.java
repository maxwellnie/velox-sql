package com.maxwellnie.velox.sql.core.utils.reflect;

import com.maxwellnie.velox.sql.core.annotation.DaoImplDeclared;
import com.maxwellnie.velox.sql.core.natives.exception.ClassTypeException;
import com.maxwellnie.velox.sql.core.natives.exception.DaoImplClassException;
import com.maxwellnie.velox.sql.core.natives.exception.RegisterMethodException;
import com.maxwellnie.velox.sql.core.proxy.executor.MethodMapRegister;
import com.maxwellnie.velox.sql.core.utils.java.StringUtils;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 反射工具类
 * @author Maxwell Nie
 */
public abstract class ReflectionUtils {

    /**
     * 获取一个Class对象的全部属性（包括父类的属性）。
     * @param beanClass
     * @return 这个类全部的属性（包括父类）
     */
    public static List<Field> getAllFields(Class<?> beanClass){
        assert beanClass!=null : "The Param beanClass maybe null.";
        List<Field> allFields = Arrays.stream(beanClass.getDeclaredFields()).filter(ReflectionUtils::filterField).collect(Collectors.toCollection(LinkedList::new));
        Class<?> superClass = beanClass.getSuperclass();
        List<Field> superClassFields = new LinkedList<>();
        while (superClass!=null && !superClass.equals(Object.class)){
            Field[] currentSuperClassDeclaredFields = superClass.getDeclaredFields();
            for (Field declaredField:currentSuperClassDeclaredFields) {
                /**
                 * 将过滤掉含有static和transient的属性添加到父类属性列表。
                 */
                if(filterField(declaredField)){
                    /**
                     * 过滤被重写的属性。
                     */
                    for (Field field:allFields) {
                        if(field.getName().equals(declaredField.getName()))
                            break;
                        else
                            superClassFields.add(declaredField);
                    }
                }
            }
            /**
             * 避免多余的操作。
             */
            if(!superClassFields.isEmpty()){
                allFields.addAll(superClassFields);
                superClassFields.clear();
            }
            /**
             * super->next
             * 指针指向当前类字节码对象的父类字节码对象。
             */
            superClass = superClass.getSuperclass();
        }
        return allFields;
    }
    /**
     * 获取一个Class对象的属性。
     * @param clazz
     * @param fieldName
     * @return 这个类全部的属性（包括父类）
     */
    public static Field getField(Class<?> clazz, String fieldName){
        Class<?> superClass = clazz;
        while (superClass!=null && !superClass.equals(Object.class)){
            try {
                for (Field declaredField:superClass.getDeclaredFields()) {
                    if(declaredField.getName().equals(fieldName)){
                        return declaredField;
                    }
                }
            } finally {
                superClass = superClass.getSuperclass();
            }
        }
        return null;
    }
    public static Unsafe getUnsafe(){
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 过滤掉含有static和transient的属性。
     * @param field
     * @return
     */
    private static boolean filterField(Field field){
        return !Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers());
    }
    /**
     * 注册DaoImpl接口
     * @param clazz
     * @throws ClassTypeException
     * @throws RegisterMethodException
     */
    public static void registerDaoImpl(Class<?> clazz, Object[] args) throws ClassTypeException, RegisterMethodException {
        assert clazz != null : "DaoImplInterface must not be null!";
        if (clazz.isAnnotationPresent(DaoImplDeclared.class)) {
            DaoImplDeclared daoImplDeclared = clazz.getDeclaredAnnotation(DaoImplDeclared.class);
            if (daoImplDeclared.value() != null) {
                try {
                    MethodMapRegister methodMapRegister = daoImplDeclared.value().newInstance();
                    methodMapRegister.registerDaoImpl(clazz,args);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RegisterMethodException(e);
                }
            } else
                throw new DaoImplClassException("Your supported DaoImplInterface not set ["+MethodMapRegister.class.getName()+"]");
        }
    }
    /**
     * 获取一个Class对象的全部接口（包括父类的接口）。
     * @param targetClass
     * @return 这个类全部的接口（包括父类）
     */
    public static List<Class<?>> getAllInterfaces(Class<?> targetClass){
        List<Class<?>> interfaces = new LinkedList<>(Arrays.asList(targetClass.getInterfaces()));
        Class<?> superClass = targetClass;
        while (superClass!=null && !superClass.equals(Object.class)){
            for (Class<?> interfaceClass:superClass.getInterfaces()) {
                if (!interfaces.contains(interfaceClass))
                    interfaces.add(interfaceClass);
            }
            superClass = superClass.getSuperclass();
        }
        return interfaces;
    }
    /**
     * 获取一个对象的指定方法，寻找范围包括父类、接口。
     * @param clazz
     * @return 第一个寻找到的方法（包括父类、接口）
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes){
        assert clazz!= null : "The Param clazz maybe null.";
        assert methodName!= null : "The Param methodName maybe null.";
        assert parameterTypes!= null : "The Param parameterTypes maybe null.";
        Method declaredMethod = null;
        Class<?> currentClass = clazz;
        while (currentClass != null && !currentClass.equals(Object.class)){
            Method[] currentClassDeclaredMethods = currentClass.getDeclaredMethods();
            for (Method currentClassDeclaredMethod:currentClassDeclaredMethods) {
                if (currentClassDeclaredMethod.getName().equals(methodName) && Arrays.equals(currentClassDeclaredMethod.getParameterTypes(), parameterTypes)) {
                    declaredMethod = currentClassDeclaredMethod;
                    break;
                }
            }
            if (declaredMethod == null){
                for (Class<?> interfaceClass:getAllInterfaces(currentClass)) {
                    Method[] interfaceClassDeclaredMethods = interfaceClass.getDeclaredMethods();
                    for (Method interfaceDeclaredMethod:interfaceClassDeclaredMethods) {
                        if (interfaceDeclaredMethod.getName().equals(methodName) && Arrays.equals(interfaceDeclaredMethod.getParameterTypes(), parameterTypes)) {
                            declaredMethod = interfaceDeclaredMethod;
                            break;
                        }
                    }
                }
            }
            if (declaredMethod == null)
                currentClass = currentClass.getSuperclass();
            else
                break;
        }
        return declaredMethod;
    }
    /**
     * 获取一个对象的指定方法，寻找范围包括当前类的所有接口以及父类的所有接口。
     * @param clazz
     * @return 第一个寻找到的方法
     */
    public static Method getInterfaceDeclaredMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        assert clazz!= null : "The Param clazz maybe null.";
        assert methodName!= null : "The Param methodName maybe null.";
        assert parameterTypes!= null : "The Param parameterTypes maybe null.";
        Method declaredMethod = null;
            for (Class<?> interfaceClass:getAllInterfaces(clazz)) {
                Method[] interfaceClassDeclaredMethods = interfaceClass.getDeclaredMethods();
                for (Method interfaceDeclaredMethod:interfaceClassDeclaredMethods) {
                    if (interfaceDeclaredMethod.getName().equals(methodName) && Arrays.equals(interfaceDeclaredMethod.getParameterTypes(), parameterTypes)) {
                        declaredMethod = interfaceDeclaredMethod;
                        break;
                    }
                }
            }
        return declaredMethod;
    }
    /**
     * 获取一个字节码对象的所有接口声明的方法。
     * @param clazz
     * @return
     */
    public static List<Method> getInterfaceAllDeclaredMethods(Class<?> clazz) {
        assert clazz!= null : "The Param clazz maybe null.";
        List<Method> declaredMethods = new LinkedList<>();
        if(clazz.isInterface()){
            Class<?> currentInterfaceClass =  clazz;
            while (currentInterfaceClass != null && !currentInterfaceClass.equals(Object.class)){
                for (Method declaredMethod:currentInterfaceClass.getDeclaredMethods()) {
                    if (!declaredMethods.contains(declaredMethod) && !declaredMethod.isDefault())
                        declaredMethods.add(declaredMethod);
                }
                currentInterfaceClass = currentInterfaceClass.getSuperclass();
            }
        }else{
            List<Class<?>> interfaces = getAllInterfaces(clazz);
            for (Class<?> interfaceClass:interfaces) {
                for (Method declaredMethod:interfaceClass.getDeclaredMethods()) {
                    if (!declaredMethods.contains(declaredMethod) && !declaredMethod.isDefault())
                        declaredMethods.add(declaredMethod);
                }
            }
        }
        return declaredMethods;
    }
    /**
     * 判断一个Class对象是否实现了某个接口。
     * @param targetClass
     * @param interfaceClass
     * @return
     */
    public static boolean hasInterface(Class<?> targetClass, Class<?> interfaceClass){
        if(interfaceClass.isInterface()){
            for (Class<?> currentInterfaceClass:getAllInterfaces(targetClass)) {
                if (currentInterfaceClass.equals(interfaceClass))
                    return true;
            }
        }
        return false;
    }
    /**
     * 通过反射创建一个对象。
     * @param clazz
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static<T> T newInstance(Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return (T)clazz.getConstructor().newInstance();
    }
    /**
     * 获取一个字段的setter方法。
     * @param clazz
     * @param field
     * @return
     */
    public static Method getSetterMethod(Class<?> clazz, Field field) {
        Method method = getMethod(clazz, "set"+ StringUtils.toFirstUpperCase(field.getName()), field.getType());
        if(method == null && (field.getType() == boolean.class || field.getType() == Boolean.class)){
            method = getMethod(clazz, "set"+ StringUtils.toFirstUpperCase(field.getName().substring(2)), field.getType());
        }
        return method;
    }
    /**
     * 获取一个字段的getter方法。
     * @param clazz
     * @param field
     * @return
     */
    public static Method getGetterMethod(Class<?> clazz, Field field) {
        Method method = getMethod(clazz, "get"+ StringUtils.toFirstUpperCase(field.getName()));
        if(method == null && (field.getType() == boolean.class || field.getType() == Boolean.class)){
            method = getMethod(clazz, "is"+ StringUtils.toFirstUpperCase(field.getName()));
            if (method == null)
                method = getMethod(clazz, field.getName());
        }
        return method;
    }
    private static final Map<Field, MetaField> META_FIELD_CACHE = new HashMap<>();
    private static final Lock LOCK = new ReentrantLock();
    /**
     * 获取一个类的所有字段的元数据对象。
     * @param clazz
     * @return Map<String, MetaField>
     */
    public static Map<String, MetaField> getMetaFieldsMap(Class<?> clazz) {
        Map<String, MetaField> metaFields = new HashMap<>();
        for (Field field:getAllFields(clazz)) {
            metaFields.put(field.getName(), getMetaField(clazz, field));
        }
        return metaFields;
    }
    /**
     * 获取一个类的所有字段的元数据对象。
     * @param clazz
     * @return List<MetaField>
     */
    public static List<MetaField> getMetaFields(Class<?> clazz) {
        List<MetaField> metaFields = new ArrayList<>();
        for (Field field:getAllFields(clazz)) {
            metaFields.add(getMetaField(clazz, field));
        }
        return metaFields;
    }
    /**
     * 获取一个字段的元数据对象。
     * @param clazz
     * @param field
     * @return MetaField
     */
    public static MetaField getMetaField(Class<?> clazz, Field field){
        if(META_FIELD_CACHE.containsKey(field))
            return META_FIELD_CACHE.get(field);
        else {
            LOCK.lock();
            if(META_FIELD_CACHE.containsKey(field))
                return META_FIELD_CACHE.get(field);
            MetaField metaField = new MetaField();
            metaField.field = field;
            if(Collection.class.isAssignableFrom(field.getType()))
                metaField.isCollection = true;
            metaField.setter = getSetterMethod(clazz, field);
            metaField.getter = getGetterMethod(clazz, field);
            META_FIELD_CACHE.put(field, metaField);
            LOCK.unlock();
            return metaField;
        }
    }
}
