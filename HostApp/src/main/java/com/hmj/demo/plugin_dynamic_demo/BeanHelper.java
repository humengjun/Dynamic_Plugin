package com.hmj.demo.plugin_dynamic_demo;

import com.hmj.demo.sharelibrary.IBean;

import dalvik.system.PathClassLoader;

public class BeanHelper {
    private static IBean iBean;

    public static void initBean(PathClassLoader classLoader, IBean.Listener listener) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Class<?> beanClass = classLoader.loadClass("com.hmj.demo.plugin1.Bean");
        iBean = (IBean) beanClass.newInstance();
        iBean.setListener(listener);
    }

    public static void setValue(String value) {
        iBean.setValue(value);
    }

    public static String getValue() {
        return iBean.getValue();
    }
}
