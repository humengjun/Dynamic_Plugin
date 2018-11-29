package com.hmj.demo.plugin2;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.hmj.demo.sharelibrary.IBean;

public class PluginBindService extends Service {

    private Binder binder = new PluginBinder();

    class PluginBinder extends Binder implements IBean {
        String value = "Plugin2";
        @Override
        public String getValue() {
            return value;
        }

        @Override
        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public void setListener(Listener listener) {

        }
    }

    public PluginBindService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this,"Plugin2 onBind!",Toast.LENGTH_SHORT).show();
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this,"Plugin2 onCreate!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this,"Plugin2 onDestroy!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this,"Plugin2 onUnbind!",Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }
}
