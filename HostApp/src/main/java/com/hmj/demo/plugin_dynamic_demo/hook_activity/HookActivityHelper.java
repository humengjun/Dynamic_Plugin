package com.hmj.demo.plugin_dynamic_demo.hook_activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;

import com.hmj.demo.plugin_dynamic_demo.StubActivity;
import com.hmj.demo.plugin_dynamic_demo.utils.Reflection;
import com.hmj.demo.plugin_dynamic_demo.utils.Utils;
import com.hmj.demo.sharelibrary.helper.RefInvoke;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class HookActivityHelper {

    public static Map<String, Object> sLoadedApk = new HashMap<>();


    /**
     * 外观模式
     * 对所有Activity进行hook
     *
     * @param context
     */
    public static void initActivityHook(Context context) throws Exception {
        hookActivityManagerNative();
        hookPackageManager(context);
        hookActivityThread();
    }

    /**
     * hook ActivityManagerNative
     *
     * @throws ClassNotFoundException
     */
    private static void hookActivityManagerNative() throws ClassNotFoundException {
        //获取AMN的gDefault单例gDefault，gDefault是final静态的
        Object gDefault = RefInvoke.getStaticFieldObject("android.app.ActivityManagerNative", "gDefault");

        // gDefault是一个 android.util.Singleton<T>对象; 我们取出这个单例里面的mInstance字段
        Object mInstance = RefInvoke.getFieldObject("android.util.Singleton", gDefault, "mInstance");

        // 创建一个这个对象的代理对象MockClass1, 然后替换这个字段, 让我们的代理对象帮忙干活
        Class<?> classB2Interface = Class.forName("android.app.IActivityManager");
        Object proxy = Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{classB2Interface},
                HookAMSHandler.getInstance(mInstance));

        //把gDefault的mInstance字段，修改为proxy
        RefInvoke.setFieldObject("android.util.Singleton", gDefault, "mInstance", proxy);
    }

    /**
     * hook PMS
     *
     * @param context
     * @throws ClassNotFoundException
     */
    private static void hookPackageManager(Context context) throws Exception {
        //获取全局的ActivityThread对象
        Object currentActivityThread = Reflection.getStaticFieldObject("android.app.ActivityThread", "sCurrentActivityThread");
        //获取sPackageManager
        Object sPackageManager = Reflection.getInstanceFieldObject(currentActivityThread, "android.app.ActivityThread", "sPackageManager");
        //创建代理对象
        Class<?> iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager");
        Object proxy = Proxy.newProxyInstance(iPackageManagerInterface.getClassLoader(),
                new Class<?>[]{iPackageManagerInterface},
                HookPMSHandler.getInstance(sPackageManager, context.getPackageName(), StubActivity.class.getName()));
        //替换掉ActivityThread里面的sPackageManager字段
        Reflection.setInstanceFieldObject(currentActivityThread, "android.app.ActivityThread", "sPackageManager", proxy);
        //替换掉ApplicationPackageManager里面的mPM对象
        PackageManager pm = context.getPackageManager();
        Reflection.setInstanceFieldObject(pm, "mPM", proxy);

    }

    /**
     * hook Instrumentation
     *
     * @param activity
     */
    private static void hookInstrumentation(Activity activity) {
        //获取Activity中的mInstrumentation属性
        Instrumentation mInstrumentation = (Instrumentation) Reflection.getInstanceFieldObject(activity, Activity.class, "mInstrumentation");
        //使用MyInstrumentation替换
        Reflection.setInstanceFieldObject(activity, Activity.class, "mInstrumentation", new HookInstrumentation(mInstrumentation));
    }

    /**
     * hook H
     */
    private static void hookActivityThread() {
        // 先获取到当前的ActivityThread对象
        Object currentActivityThread = RefInvoke.getStaticFieldObject("android.app.ActivityThread", "sCurrentActivityThread");

        // 由于ActivityThread一个进程只有一个,我们获取这个对象的mH
        Handler mH = (Handler) RefInvoke.getFieldObject(currentActivityThread, "mH");


        //把Handler的mCallback字段，替换为new MockClass2(mH)
        RefInvoke.setFieldObject(Handler.class, mH, "mCallback", new HookCallback(mH));
    }

    /**
     * hook LoadedApk
     *
     * @param apkFile
     */
    private static void hookLoadedApk(File apkFile) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        // 先获取到当前的ActivityThread对象
        Object currentActivityThread = RefInvoke.getStaticFieldObject("android.app.ActivityThread", "sCurrentActivityThread");

        //获取到mPackages这个成员变量，这里缓存了dex包的信息
        Map mPackages = (Map) Reflection.getInstanceFieldObject(currentActivityThread, "mPackages");

        //准备两个参数
        //android.content.res.CompatibilityInfo
        Object defaultCompatibilityInfo = Reflection.getStaticFieldObject("android.content.res.CompatibilityInfo",
                "DEFAULT_COMPATIBILITY_INFO");
        //从apk获取ApplicationInfo
        ApplicationInfo applicationInfo = generateApplicationInfo(apkFile);

        //调用ActivityThread的getPackageInfoNoCheck方法得到LoadedApk
        Class[] p1 = {ApplicationInfo.class, Class.forName("android.content.res.CompatibilityInfo")};
        Object[] v1 = {applicationInfo, defaultCompatibilityInfo};
        Object loadedApk = Reflection.invokeInstanceMethod(currentActivityThread, "getPackageInfoNoCheck", p1, v1);

        //为插件新建一个ClassLoader
        String odexPath = Utils.getPluginOptDexDir(applicationInfo.packageName).getPath();
        String libDir = Utils.getPluginLibDir(applicationInfo.packageName).getPath();

        ClassLoader classLoader = new CustomClassLoader(apkFile.getPath(), odexPath, libDir, ClassLoader.getSystemClassLoader());
        Reflection.setInstanceFieldObject(loadedApk, "mClassLoader", classLoader);

        //把插件的LoadedApk对象放入缓存
        WeakReference weakReference = new WeakReference(loadedApk);
        mPackages.put(applicationInfo.packageName, weakReference);

        //由于是弱引用，因此需要备份，避免被GC
        sLoadedApk.put(applicationInfo.packageName, loadedApk);
    }

    private static ApplicationInfo generateApplicationInfo(File apkFile) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        //获得核心类：android.content.pm.PackageParser
        Class<?> packageParserClass = Class.forName("android.content.pm.PackageParser");
        Class<?> packageParser$PackageClass = Class.forName("android.content.pm.PackageParser$Package");
        Class<?> packageUserStateClass = Class.forName("android.content.pm.PackageUserState");

        //得到generateApplicationInfo方法
        //参数Package p，int flags，PackageUserState state
        //API 23

        //创建PackageParser对象
        Object packageParser = packageParserClass.newInstance();

        //调用PackageParser.parsePackage解析apk的信息
        //返回Package对象
        Class[] p1 = {File.class, int.class};
        Object[] v1 = {apkFile, 0};
        Object packageObj = Reflection.invokeInstanceMethod(packageParser, "parsePackage", p1, v1);

        //得到第三个参数PackageUserState
        Object defaultPackageUserState = packageUserStateClass.newInstance();

        //调用generateApplicationInfo方法
        Class[] p2 = {packageParser$PackageClass, int.class, packageUserStateClass};
        Object[] v2 = {packageObj, 0, defaultPackageUserState};
        ApplicationInfo applicationInfo = (ApplicationInfo) Reflection.invokeInstanceMethod(packageParser,
                "generateApplicationInfo", p2, v2);

        String apkPath = apkFile.getPath();
        applicationInfo.sourceDir = apkPath;
        applicationInfo.publicSourceDir = apkPath;
        return applicationInfo;
    }
}
