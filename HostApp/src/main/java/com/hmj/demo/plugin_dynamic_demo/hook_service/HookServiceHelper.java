package com.hmj.demo.plugin_dynamic_demo.hook_service;

import android.os.Handler;

import com.hmj.demo.sharelibrary.helper.RefInvoke;

import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class HookServiceHelper {

    public static LinkedList<Class> stubServiceClasses = new LinkedList<>();
    public static LinkedHashMap<String,String> pluginServices = new LinkedHashMap<>();


    /**
     * 外观模式
     * 对所有service进行hook
     */
    public static void initServiceHook() throws ClassNotFoundException {
        stubServiceClasses.add(StubService1.class);
        stubServiceClasses.add(StubService2.class);
        stubServiceClasses.add(StubService3.class);
        stubServiceClasses.add(StubService4.class);
        stubServiceClasses.add(StubService5.class);
        stubServiceClasses.add(StubService6.class);
        stubServiceClasses.add(StubService7.class);
        stubServiceClasses.add(StubService8.class);
        stubServiceClasses.add(StubService9.class);
        stubServiceClasses.add(StubService10.class);

        hookActivityManagerNative();
        hookActivityThread();
    }

    /**
     * hook ActivityManagerNative
     *
     * @throws ClassNotFoundException
     */
    private static void hookActivityManagerNative() throws ClassNotFoundException {
        //获取AMN的gDefault单例gDefault，gDefault是final静态的
        Object gDefault = RefInvoke.getStaticFieldObject("android.app.ActivityManagerNative", "gDefault");

        // gDefault是一个 android.util.Singleton<T>对象; 我们取出这个单例里面的mInstance字段
        Object mInstance = RefInvoke.getFieldObject("android.util.Singleton", gDefault, "mInstance");

        // 创建一个这个对象的代理对象MockClass1, 然后替换这个字段, 让我们的代理对象帮忙干活
        Class<?> classB2Interface = Class.forName("android.app.IActivityManager");
        Object proxy = Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{classB2Interface},
                HookAMSHandler.getInstance(mInstance));

        //把gDefault的mInstance字段，修改为proxy
        RefInvoke.setFieldObject("android.util.Singleton", gDefault, "mInstance", proxy);
    }



    /**
     * hook H
     */
    private static void hookActivityThread() {
        // 先获取到当前的ActivityThread对象
        Object currentActivityThread = RefInvoke.getStaticFieldObject("android.app.ActivityThread", "sCurrentActivityThread");

        // 由于ActivityThread一个进程只有一个,我们获取这个对象的mH
        Handler mH = (Handler) RefInvoke.getFieldObject(currentActivityThread, "mH");


        //把Handler的mCallback字段，替换为new MockClass2(mH)
        RefInvoke.setFieldObject(Handler.class, mH, "mCallback", new HookCallback(mH));
    }

}
