package com.hmj.demo.plugin_dynamic_demo.hook_service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class StubService7 extends Service {
    public StubService7() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
