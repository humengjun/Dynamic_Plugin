package com.hmj.demo.plugin_dynamic_demo.hook_service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class StubService4 extends Service {
    public StubService4() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
