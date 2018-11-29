package com.hmj.demo.plugin1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hmj.demo.sharelibrary.helper.ToastUtils;

public class PluginReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("host_message");
        //接收到广播
        ToastUtils.shortToast(context, "Plugin1:" + intent.getAction()+":"+message);
    }
}
