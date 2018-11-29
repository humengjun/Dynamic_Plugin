package com.hmj.demo.plugin_dynamic_demo.hook_service;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hmj.demo.plugin_dynamic_demo.utils.Reflection;
import com.hmj.demo.sharelibrary.helper.RefInvoke;

import static com.hmj.demo.plugin_dynamic_demo.hook_service.HookServiceHelper.pluginServices;

public class HookCallback implements Handler.Callback {
    Handler mBase;

    public HookCallback(Handler mBase) {
        this.mBase = mBase;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 100:
                handleLaunchActivity(msg);
                break;
            case 114:
                handleCreateService(msg);
                break;
        }
        mBase.handleMessage(msg);
        return true;
    }

    private void handleCreateService(Message msg) {
        //取出Activity
        Object obj = msg.obj;

        //将替身还原成真身
        ServiceInfo serviceInfo = (ServiceInfo) RefInvoke.getFieldObject(obj, "info");


        Log.d("CallBack-Hook", obj.toString());

        String realServiceName = null;

        for (String key : pluginServices.keySet()) {
            String value = pluginServices.get(key);
            if(value.equals(serviceInfo.name)) {
                realServiceName = key;
                break;
            }
        }

        serviceInfo.name = realServiceName;
    }

    private void handleLaunchActivity(Message msg) {
        //取出Activity
        Object obj = msg.obj;

        //将替身还原成真身
        Intent intent = (Intent) Reflection.getInstanceFieldObject(obj, "intent");
        Intent targetIntent = intent.getParcelableExtra("original_activity");
        if (targetIntent == null) return;
        intent.setComponent(targetIntent.getComponent());
        Log.d("CallBack-Hook", obj.toString());

        ActivityInfo activityInfo = (ActivityInfo) Reflection.getInstanceFieldObject(obj, "activityInfo");
        activityInfo.applicationInfo.packageName = targetIntent.getPackage() == null ?
                targetIntent.getComponent().getPackageName() : targetIntent.getPackage();
    }
}
