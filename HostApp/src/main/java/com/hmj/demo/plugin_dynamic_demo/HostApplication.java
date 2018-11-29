package com.hmj.demo.plugin_dynamic_demo;

import android.app.Application;
import android.content.Context;

import com.hmj.demo.plugin_dynamic_demo.hook.HookHelper;
import com.hmj.demo.sharelibrary.helper.PluginHelper;

public class HostApplication extends Application {
    private static Context baseCtx;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        baseCtx = base;

        try {
            PluginHelper.initPlugins(this);
            HookHelper.initHook(this);
//            HookActivityHelper.initActivityHook(base);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static Context getAppCtx() {
        return baseCtx;
    }
}
