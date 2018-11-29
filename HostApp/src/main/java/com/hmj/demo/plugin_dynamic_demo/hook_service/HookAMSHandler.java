package com.hmj.demo.plugin_dynamic_demo.hook_service;

import android.content.ComponentName;
import android.content.Intent;
import android.text.TextUtils;

import com.hmj.demo.plugin_dynamic_demo.StubActivity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static com.hmj.demo.plugin_dynamic_demo.hook_service.HookServiceHelper.pluginServices;
import static com.hmj.demo.plugin_dynamic_demo.hook_service.HookServiceHelper.stubServiceClasses;

public class HookAMSHandler implements InvocationHandler {
    private volatile static HookAMSHandler mInstance;
    private Object mBase;
    private String HOST_PACKAGE_NAME = "com.hmj.demo.plugin_dynamic_demo";

    private HookAMSHandler(Object mBase) {
        this.mBase = mBase;
    }

    public static HookAMSHandler getInstance(Object mBase) {
        if (mInstance == null) {
            synchronized (HookAMSHandler.class) {
                if (mInstance == null) {
                    mInstance = new HookAMSHandler(mBase);
                }
            }
        }
        return mInstance;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("startActivity")) {
            Intent raw = null;
            int index = 0;
            for (index = 0; index < args.length; index++) {
                if (args[index] instanceof Intent) {
                    raw = (Intent) args[index];
                    break;
                }
            }

            Intent newIntent = new Intent();

            //将需要启动的Activity替换成StubActivity
            ComponentName componentName = new ComponentName(HOST_PACKAGE_NAME, StubActivity.class.getName());
            newIntent.setComponent(componentName);
            //将原来的Activity保存
            newIntent.putExtra("original_activity", raw);
            //替换Intent，欺骗AMS
            args[index] = newIntent;

        } else if (method.getName().equals("startService")) {
            Intent raw = null;
            int index = 0;
            for (index = 0; index < args.length; index++) {
                if (args[index] instanceof Intent) {
                    raw = (Intent) args[index];
                    break;
                }
            }

            Intent newIntent = new Intent();

            //将需要启动的Service替换成StubService
            //需要判断当前占位的Service组件是否有空闲
            if (stubServiceClasses.size() > 0 && pluginServices.get(raw.getComponent().getClassName()) == null) {
                ComponentName componentName = new ComponentName(HOST_PACKAGE_NAME, stubServiceClasses.get(0).getName());
                newIntent.setComponent(componentName);
                pluginServices.put(raw.getComponent().getClassName(), componentName.getClassName());
                //替换Intent，欺骗AMS
                args[index] = newIntent;
            }

        }else if (method.getName().equals("bindService")) {
            Intent raw = null;
            int index = 0;
            for (index = 0; index < args.length; index++) {
                if (args[index] instanceof Intent) {
                    raw = (Intent) args[index];
                    break;
                }
            }

            Intent newIntent = new Intent();

            //将需要启动的Service替换成StubService
            //需要判断当前占位的Service组件是否有空闲
            if (stubServiceClasses.size() > 0) {
                ComponentName componentName = new ComponentName(HOST_PACKAGE_NAME, stubServiceClasses.poll().getName());
                newIntent.setComponent(componentName);
                pluginServices.put(raw.getComponent().getClassName(), componentName.getClassName());
                //替换Intent，欺骗AMS
                args[index] = newIntent;
            }

        } else if ("stopService".equals(method.getName())) {
            Intent raw = null;
            int index = 0;
            for (index = 0; index < args.length; index++) {
                if (args[index] instanceof Intent) {
                    raw = (Intent) args[index];
                    break;
                }
            }

            Intent newIntent = new Intent();

            //将需要启动的Service替换成StubService
            String stubServiceClass = pluginServices.get(raw.getComponent().getClassName());
            if (!TextUtils.isEmpty(stubServiceClass)) {
                ComponentName componentName = new ComponentName(HOST_PACKAGE_NAME, stubServiceClass);
                newIntent.setComponent(componentName);
                //服务关闭，从缓存中移除，并添加到空闲列表
                pluginServices.remove(raw.getComponent().getClassName());
                stubServiceClasses.add(Class.forName(stubServiceClass));
                //替换Intent，欺骗AMS
                args[index] = newIntent;
            }
        }
        return method.invoke(mBase, args);
    }
}
