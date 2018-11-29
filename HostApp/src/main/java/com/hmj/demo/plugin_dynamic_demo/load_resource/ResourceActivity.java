package com.hmj.demo.plugin_dynamic_demo.load_resource;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hmj.demo.plugin_dynamic_demo.R;
import com.hmj.demo.sharelibrary.IBean;
import com.hmj.demo.sharelibrary.helper.PluginHelper;
import com.hmj.demo.sharelibrary.helper.PluginItem;
import com.hmj.demo.sharelibrary.helper.RefInvoke;
import com.hmj.demo.sharelibrary.helper.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dalvik.system.PathClassLoader;

public class ResourceActivity extends BaseActivity {
    int index = 0;
    @BindView(R.id.pluginText)
    TextView pluginText;
    @BindView(R.id.pluginImg)
    ImageView pluginImg;
    @BindView(R.id.pluginLayout)
    LinearLayout pluginLayout;
    @BindView(R.id.button)
    Button button;

    private IBean iBean;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            iBean = (IBean) iBinder;
            ToastUtils.shortToast(ResourceActivity.this, iBean.getValue());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource);
        ButterKnife.bind(this);

        button.setStateListAnimator(null);
        button.setElevation(100);
        button.setBackgroundDrawable(getBg(getResources().getColor(R.color.colorAccent)));

        try {
            updateUI((PathClassLoader) getClassLoader());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.button)
    public void onViewClicked() {
        if (index == PluginHelper.plugins.size() - 1) {
            index = 0;
        } else {
            index++;
        }

        PluginItem item = PluginHelper.plugins.get(index);
        loadResource(item.getPluginPath());
        try {
            updateUI((PathClassLoader) getClassLoader());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void updateUI(PathClassLoader classLoader) throws ClassNotFoundException {
        String pluginPackageName = PluginHelper.plugins.get(index).getPluginInfo().packageName;
        Class clazz = classLoader.loadClass(pluginPackageName + ".UIUtil");

        //得到插件的strings.xml资源
        String pluginText = (String) RefInvoke.invokeStaticMethod(clazz, "getString", Context.class, this);
        this.pluginText.setText(pluginText);
        //得到插件的drawable资源
        Drawable pluginImg = (Drawable) RefInvoke.invokeStaticMethod(clazz, "getDrawable", Context.class, this);
        this.pluginImg.setImageDrawable(pluginImg);
        //得到插件的layout资源
//        View pluginLayout = (View) RefInvoke.invokeStaticMethod(clazz, "getLayout", Context.class, this);
        Class layoutClazz = classLoader.loadClass(pluginPackageName + ".R$layout");
        int resId = (int) RefInvoke.getStaticFieldObject(layoutClazz, "activity_plugin");
        Log.d("LayoutID", resId + "");
        View pluginLayout = LayoutInflater.from(this).inflate(resId, null);
        this.pluginLayout.removeAllViews();
        this.pluginLayout.addView(pluginLayout);
        //得到插件的id资源
        Class idClass = classLoader.loadClass(pluginPackageName + ".R$id");
        int btn_1 = (int) RefInvoke.getStaticFieldObject(idClass, "startActivity");
        int btn_2 = (int) RefInvoke.getStaticFieldObject(idClass, "startService");
        int btn_3 = (int) RefInvoke.getStaticFieldObject(idClass, "stopService");
        int btn_4 = (int) RefInvoke.getStaticFieldObject(idClass, "bindService");
        int btn_5 = (int) RefInvoke.getStaticFieldObject(idClass, "unbindService");
        int btn_6 = (int) RefInvoke.getStaticFieldObject(idClass, "sendBrodCast");
        int btn_7 = (int) RefInvoke.getStaticFieldObject(idClass, "useProvider");
        pluginLayout.findViewById(btn_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPluginActivity(index);
            }
        });
        pluginLayout.findViewById(btn_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPluginService(index);
            }
        });
        pluginLayout.findViewById(btn_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPluginService(index);
            }
        });
        pluginLayout.findViewById(btn_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindPluginService(index);
            }
        });
        pluginLayout.findViewById(btn_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbindPluginService(index);
            }
        });
        pluginLayout.findViewById(btn_6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(index);
            }
        });
        pluginLayout.findViewById(btn_7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useProvider(index);
            }
        });
    }

    private void useProvider(int index) {
        String providerName = PluginHelper.plugins.get(index).getPluginInfo().packageName;
        providerName = providerName.substring(providerName.lastIndexOf(".") + 1, providerName.length());
        Uri uri = Uri.parse("content://" + providerName);
//        int result = getContentResolver().delete(uri, "where", null);
        String result = getContentResolver().getType(uri);
        ToastUtils.shortToast(this, result);
    }

    private void sendBroadcast(int index) {
        String receiverName = PluginHelper.plugins.get(index).getPluginInfo().packageName + ".receiver";
        Intent intent = new Intent(receiverName);
        intent.putExtra("host_message", "This is message from host app");
        sendBroadcast(intent);
    }

    private void unbindPluginService(int index) {
        if (connection != null)
            unbindService(connection);
    }

    private void bindPluginService(int index) {
        Intent intent = new Intent();

        String serviceName = PluginHelper.plugins.get(index).getPluginInfo().packageName + ".PluginBindService";
        try {
            intent.setClass(this, Class.forName(serviceName));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (connection != null)
            bindService(intent, connection, Service.BIND_AUTO_CREATE);
    }

    private void stopPluginService(int index) {
        Intent intent = new Intent();

        String serviceName = PluginHelper.plugins.get(index).getPluginInfo().packageName + ".PluginStartService";
        try {
            intent.setClass(this, Class.forName(serviceName));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        stopService(intent);
    }

    private void startPluginService(int index) {
        Intent intent = new Intent();

        String serviceName = PluginHelper.plugins.get(index).getPluginInfo().packageName + ".PluginStartService";
        try {
            intent.setClass(this, Class.forName(serviceName));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        startService(intent);
    }

    private void startPluginActivity(int index) {
        Intent intent = new Intent();

        String activityName = PluginHelper.plugins.get(index).getPluginInfo().packageName + ".PluginActivity";
        try {
            intent.setClass(this, Class.forName(activityName));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        startActivity(intent);
    }

    public Drawable getBg(int bgcolor) {
        //阴影部分
        GradientDrawable elevationDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#C5C5C5"), Color.parseColor("#777777")});
        elevationDrawable.setBounds(0, 0, 0, 0);
        elevationDrawable.setShape(GradientDrawable.RECTANGLE);
        elevationDrawable.setCornerRadius(5);
        //背景部分
        //正常状态
        GradientDrawable backgroundDrawable_normal = new GradientDrawable();
        backgroundDrawable_normal.setColor(bgcolor);
        backgroundDrawable_normal.setShape(GradientDrawable.RECTANGLE);
        backgroundDrawable_normal.setCornerRadius(5);
        //按下状态
        GradientDrawable backgroundDrawable_press = new GradientDrawable();
        backgroundDrawable_press.setColor(Color.parseColor("#ff8c00"));
        backgroundDrawable_press.setShape(GradientDrawable.RECTANGLE);
        backgroundDrawable_press.setCornerRadius(5);
        LayerDrawable layer_press = new LayerDrawable(new Drawable[]{elevationDrawable, backgroundDrawable_press});
        layer_press.setLayerInset(1, 0, 0, 2, 5);
        LayerDrawable layer_normal = new LayerDrawable(new Drawable[]{elevationDrawable, backgroundDrawable_normal});
        layer_normal.setLayerInset(1, 0, 0, 2, 5);

        return layer_normal;
    }
}
