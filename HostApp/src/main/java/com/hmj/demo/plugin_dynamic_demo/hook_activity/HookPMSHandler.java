package com.hmj.demo.plugin_dynamic_demo.hook_activity;

import android.content.ComponentName;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

public class HookPMSHandler implements InvocationHandler {
    private volatile static HookPMSHandler mInstance;
    private Object mBase;
    private String pmName;
    private String hostClazzName;

    private HookPMSHandler(Object mBase, String pmName, String hostClazzName) {
        this.mBase = mBase;
        this.pmName = pmName;
        this.hostClazzName = hostClazzName;
    }

    public static HookPMSHandler getInstance(Object mBase, String pmName, String hostClazzName) {
        if (mInstance == null) {
            synchronized (HookPMSHandler.class) {
                if (mInstance == null) {
                    mInstance = new HookPMSHandler(mBase,pmName,hostClazzName);
                }
            }
        }
        return mInstance;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if(args[0] instanceof ComponentName){
            //将需要启动的Activity替换成StubActivity
            ComponentName componentName = new ComponentName(pmName, hostClazzName);
            args[0] = componentName;
        }
        Log.d("PMS-Hook", Arrays.toString(args));
        return method.invoke(mBase,args);
    }
}
