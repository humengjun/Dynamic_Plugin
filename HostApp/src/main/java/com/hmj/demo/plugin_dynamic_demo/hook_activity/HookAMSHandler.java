package com.hmj.demo.plugin_dynamic_demo.hook_activity;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

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
            Intent raw;
            int index = 0;

            for (index = 0; index < args.length; index++) {
                if (args[index] instanceof Intent) {
                    break;
                }
            }
            raw = (Intent) args[index];

            Intent newIntent = new Intent();
            //获取替换的Activity包名
            String stubPackageName = raw.getComponent().getPackageName();

            //将需要启动的Activity替换成StubActivity
            ComponentName componentName = new ComponentName(HOST_PACKAGE_NAME, StubActivity.class.getName());
            newIntent.setComponent(componentName);
            //将原来的Activity保存
            newIntent.putExtra("original_activity", raw);
            //替换Intent，欺骗AMS
            args[index] = newIntent;

            Log.d("AMS-Hook", "Hook success!");
        }
        return method.invoke(mBase, args);
    }
}
