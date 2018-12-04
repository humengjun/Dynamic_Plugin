package com.hmj.demo.plugin_dynamic_demo.hook_service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ProxyService extends Service {
    public ProxyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return ServiceManager.getInstance().onBind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return ServiceManager.getInstance().onStartCommand(intent, flags, startId);
    }
}
