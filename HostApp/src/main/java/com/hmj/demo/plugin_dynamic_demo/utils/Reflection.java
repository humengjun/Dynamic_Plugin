package com.hmj.demo.plugin_dynamic_demo.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflection {

    /**
     * 无参构造器
     *
     * @param className
     * @return
     */
    public static Object createObject(String className) {
        Class[] paramTypes = {};
        Object[] paramValues = {};

        try {
            Class clazz = Class.forName(className);
            return createObject(clazz, paramTypes, paramValues);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 单参构造器
     *
     * @param className
     * @param paramType
     * @param paramValue
     * @return
     */
    public static Object createObject(String className, Class paramType, Object paramValue) {
        Class[] paramTypes = {paramType};
        Object[] paramValues = {paramValue};

        try {
            Class clazz = Class.forName(className);
            return createObject(clazz, paramTypes, paramValues);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 多参构造器
     *
     * @param className
     * @param paramTypes
     * @param paramValues
     * @return
     */
    public static Object createObject(String className, Class[] paramTypes, Object[] paramValues) {
        try {
            Class clazz = Class.forName(className);
            return createObject(clazz, paramTypes, paramValues);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 多参构造器
     *
     * @param clazz
     * @param paramTypes
     * @param paramValues
     * @return
     */
    public static Object createObject(Class clazz, Class[] paramTypes, Object[] paramValues) {
        try {
            Constructor constructor = clazz.getDeclaredConstructor(paramTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(paramValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 调用所有实例方法
     *
     * @param obj
     * @param className
     * @param methodName
     * @param paramTypes
     * @param paramValues
     * @return
     */
    public static Object invokeInstanceMethod(Object obj, String className, String methodName,
                                              Class[] paramTypes, Object[] paramValues) {
        try {
            return invokeInstanceMethod(obj,Class.forName(className),methodName,paramTypes,paramValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 调用所有实例方法
     *
     * @param obj
     * @param methodName
     * @param paramTypes
     * @param paramValues
     * @return
     */
    public static Object invokeInstanceMethod(Object obj, String methodName,
                                              Class[] paramTypes, Object[] paramValues) {
        try {
            return invokeInstanceMethod(obj,obj.getClass(),methodName,paramTypes,paramValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 调用所有实例方法
     *
     * @param obj
     * @param clazz
     * @param methodName
     * @param paramTypes
     * @param paramValues
     * @return
     */
    public static Object invokeInstanceMethod(Object obj, Class clazz, String methodName,
                                              Class[] paramTypes, Object[] paramValues) {
        if (obj == null) {
            return null;
        }
        try {
            Method method = clazz.getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
            return method.invoke(obj, paramValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 调用所有静态方法
     *
     * @param className
     * @param methodName
     * @param paramTypes
     * @param paramValues
     * @return
     */
    public static Object invokeStaticMethod(String className, String methodName, Class[] paramTypes,
                                            Object[] paramValues) {
        try {
            Method method = Class.forName(className).getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
            return method.invoke(null, paramValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取所有实例属性
     *
     * @param obj
     * @param className
     * @param fieldName
     * @return
     */
    public static Object getInstanceFieldObject(Object obj, String className, String fieldName) {
        try {
            return getInstanceFieldObject(obj, Class.forName(className), fieldName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取所有实例属性
     *
     * @param obj
     * @param fieldName
     * @return
     */
    public static Object getInstanceFieldObject(Object obj, String fieldName) {
        return getInstanceFieldObject(obj, obj.getClass(), fieldName);
    }

    /**
     * 获取所有实例属性
     *
     * @param obj
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Object getInstanceFieldObject(Object obj, Class clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置所有实例属性
     *
     * @param obj
     * @param className
     * @param fieldName
     * @param fieldValue
     */
    public static void setInstanceFieldObject(Object obj, String className, String fieldName, Object fieldValue) {
        try {
            setInstanceFieldObject(obj, Class.forName(className), fieldName, fieldValue);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置所有实例属性
     *
     * @param obj
     * @param fieldName
     * @param fieldValue
     */
    public static void setInstanceFieldObject(Object obj, String fieldName, Object fieldValue) {
        setInstanceFieldObject(obj, obj.getClass(), fieldName, fieldValue);
    }

    /**
     * 设置所有实例属性
     *
     * @param obj
     * @param clazz
     * @param fieldName
     * @param fieldValue
     */
    public static void setInstanceFieldObject(Object obj, Class clazz, String fieldName, Object fieldValue) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, fieldValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取所有静态属性
     *
     * @param className
     * @param fieldName
     * @return
     */
    public static Object getStaticFieldObject(String className, String fieldName) {
        try {
            Field field = Class.forName(className).getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置所有静态属性
     *
     * @param className
     * @param fieldName
     * @param fieldValue
     */
    public static void setStaticFieldObject(String className, String fieldName, Object fieldValue) {
        try {
            Field field = Class.forName(className).getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, fieldValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
