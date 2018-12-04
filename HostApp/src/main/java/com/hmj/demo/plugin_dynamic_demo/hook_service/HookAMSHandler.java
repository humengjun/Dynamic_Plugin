package com.hmj.demo.plugin_dynamic_demo.hook_service;

import android.content.ComponentName;
import android.content.Intent;

import com.hmj.demo.plugin_dynamic_demo.HostApplication;
import com.hmj.demo.plugin_dynamic_demo.StubActivity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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

        }
        //基于动态Hook的Service插件化方案
//        else if (method.getName().equals("startService")) {
//            Intent raw = null;
//            int index = 0;
//            for (index = 0; index < args.length; index++) {
//                if (args[index] instanceof Intent) {
//                    raw = (Intent) args[index];
//                    break;
//                }
//            }
//
//            Intent newIntent = new Intent();
//
//            //将需要启动的Service替换成StubService
//            //需要判断当前占位的Service组件是否有空闲
//            if (stubServiceClasses.size() > 0 && pluginServices.get(raw.getComponent().getClassName()) == null) {
//                ComponentName componentName = new ComponentName(HOST_PACKAGE_NAME, stubServiceClasses.get(0).getName());
//                newIntent.setComponent(componentName);
//                pluginServices.put(raw.getComponent().getClassName(), componentName.getClassName());
//                //替换Intent，欺骗AMS
//                args[index] = newIntent;
//            }
//
//        }else if (method.getName().equals("bindService")) {
//            Intent raw = null;
//            int index = 0;
//            for (index = 0; index < args.length; index++) {
//                if (args[index] instanceof Intent) {
//                    raw = (Intent) args[index];
//                    break;
//                }
//            }
//
//            Intent newIntent = new Intent();
//
//            //将需要启动的Service替换成StubService
//            //需要判断当前占位的Service组件是否有空闲
//            if (stubServiceClasses.size() > 0) {
//                ComponentName componentName = new ComponentName(HOST_PACKAGE_NAME, stubServiceClasses.poll().getName());
//                newIntent.setComponent(componentName);
//                pluginServices.put(raw.getComponent().getClassName(), componentName.getClassName());
//                //替换Intent，欺骗AMS
//                args[index] = newIntent;
//            }
//
//        } else if ("stopService".equals(method.getName())) {
//            Intent raw = null;
//            int index = 0;
//            for (index = 0; index < args.length; index++) {
//                if (args[index] instanceof Intent) {
//                    raw = (Intent) args[index];
//                    break;
//                }
//            }
//
//            Intent newIntent = new Intent();
//
//            //将需要启动的Service替换成StubService
//            String stubServiceClass = pluginServices.get(raw.getComponent().getClassName());
//            if (!TextUtils.isEmpty(stubServiceClass)) {
//                ComponentName componentName = new ComponentName(HOST_PACKAGE_NAME, stubServiceClass);
//                newIntent.setComponent(componentName);
//                //服务关闭，从缓存中移除，并添加到空闲列表
//                pluginServices.remove(raw.getComponent().getClassName());
//                stubServiceClasses.add(Class.forName(stubServiceClass));
//                //替换Intent，欺骗AMS
//                args[index] = newIntent;
//            }
//        }
//
        //基于动静结合的Service插件化方案
        else if ("startService".equals(method.getName())) {
            // 找到参数里面的第一个Intent 对象
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }

            Intent rawIntent = (Intent) args[index];
            // 代理Service的包名, 也就是我们自己的包名
            String stubPackage = HostApplication.getAppCtx().getPackageName();

            // replace Plugin Service of ProxyService
            ComponentName componentName = new ComponentName(stubPackage, ProxyService.class.getName());
            Intent newIntent = new Intent();
            newIntent.setComponent(componentName);

            // 把我们原始要启动的TargetService先存起来
            newIntent.putExtra("original_service", rawIntent);
            // Replace Intent, cheat AMS
            args[index] = newIntent;
        }
        else if ("stopService".equals(method.getName())) {
            // 找到参数里面的第一个Intent 对象
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }
            Intent rawIntent = (Intent) args[index];
            return ServiceManager.getInstance().stopService(rawIntent);
        }
        else if("bindService".equals(method.getName())) {
            // 找到参数里面的第一个Intent 对象
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }
            Intent rawIntent = (Intent) args[index];
            //stroe intent-conn
            ServiceManager.getInstance().mBindServiceMap.put(args[4], rawIntent);

            // 代理Service的包名, 也就是我们自己的包名
            String stubPackage = HostApplication.getAppCtx().getPackageName();
            // replace Plugin Service of ProxyService
            ComponentName componentName = new ComponentName(stubPackage, ProxyService.class.getName());
            Intent newIntent = new Intent();
            newIntent.setComponent(componentName);
            // 把我们原始要启动的Service先存起来
            newIntent.putExtra("original_service", rawIntent);
            // Replace Intent, cheat AMS
            args[index] = newIntent;
        }
        else if("unbindService".equals(method.getName())) {
            Intent rawIntent = ServiceManager.getInstance().mBindServiceMap.get(args[0]);
            ServiceManager.getInstance().mBindServiceMap.remove(args[0]);
            ServiceManager.getInstance().onUnbind(rawIntent);
        }

        return method.invoke(mBase, args);
    }
}
