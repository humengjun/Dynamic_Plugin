package com.hmj.demo.plugin_dynamic_demo.hook_broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

import com.hmj.demo.sharelibrary.helper.PluginHelper;
import com.hmj.demo.sharelibrary.helper.PluginItem;
import com.hmj.demo.sharelibrary.helper.RefInvoke;

import java.io.File;
import java.util.List;

public class HookReceiverHelper {


    /**
     * 外观模式
     * 对所有broadcast进行hook
     * @param context
     */
    public static void initReceiverHook(Context context){
        for (PluginItem item: PluginHelper.plugins) {
            loadReceivers(context,new File(item.getPluginPath()));
        }
    }

    /**
     * 解析插件Apk文件中的 <receiver>, 并存储起来
     *
     * @param apkFile
     * @throws Exception
     */
    private static void loadReceivers(Context context, File apkFile) {
        // 首先调用parsePackage获取到apk对象对应的Package对象
        Object packageParser = RefInvoke.createObject("android.content.pm.PackageParser");
        Class[] p1 = {File.class, int.class};
        Object[] v1 = {apkFile, PackageManager.GET_RECEIVERS};
        Object packageObj = RefInvoke.invokeInstanceMethod(packageParser, "parsePackage", p1, v1);

        // 读取Package对象里面的receivers字段,注意这是一个 List<Activity> (没错,底层把<receiver>当作<activity>处理)
        // 接下来要做的就是根据这个List<Activity> 获取到Receiver对应的 ActivityInfo (依然是把receiver信息用activity处理了)
        List receivers = (List) RefInvoke.getFieldObject(packageObj, "receivers");

        for (Object receiver : receivers) {
            registerDynamicReceiver(context, receiver);
        }
    }

    // 解析出 receiver以及对应的 intentFilter
    // 手动注册Receiver
    private static void registerDynamicReceiver(Context context, Object receiver) {
        //取出receiver的intents字段
        List<? extends IntentFilter> filters = (List<? extends IntentFilter>) RefInvoke.getFieldObject(
                "android.content.pm.PackageParser$Component", receiver, "intents");

        try {
            // 把解析出来的每一个静态Receiver都注册为动态的
            for (IntentFilter intentFilter : filters) {
                ActivityInfo receiverInfo = (ActivityInfo) RefInvoke.getFieldObject(receiver, "info");

                BroadcastReceiver broadcastReceiver = (BroadcastReceiver) RefInvoke.createObject(receiverInfo.name);
                context.registerReceiver(broadcastReceiver, intentFilter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
