package com.hmj.demo.sharelibrary.helper;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexClassLoader;

public class PluginHelper {

    //当前进程的Context
    public static volatile Context mAppCtx;
    //ContextImpl中的LoadedAPK对象的mPackageInfo
    private static volatile Object mPackageInfo;

    public static final List<PluginItem> plugins = new ArrayList<>();

    public static volatile Resources mNowResources;


    /**
     * 初始化插件
     *
     * @param app
     */
    public static void initPlugins(Application app) {
        //初始化一些成员变量和加载已安装的插件
        mAppCtx = app.getBaseContext();
        mPackageInfo = RefInvoke.getFieldObject(mAppCtx, "mPackageInfo");

        try {
            AssetManager assetManager = app.getAssets();
            String[] paths = assetManager.list("");

            ArrayList<String> pluginPaths = new ArrayList<>();
            for (String path : paths) {
                if (path.endsWith(".apk")) {
                    //读取apk文件
                    extractAssets(mAppCtx, path);

                    File apkFile = mAppCtx.getFileStreamPath(path);
                    File dexFile = mAppCtx.getFileStreamPath(path.replace(".apk", ".dex"));
//                    File dexFile = mAppCtx.getDir("dex", 0);

                    //合并插件的dex文件
                    mergeDex(apkFile, dexFile);

                    DexClassLoader classLoader = new DexClassLoader(apkFile.getPath(),dexFile.getAbsolutePath(),null,mAppCtx.getClassLoader());

                    PluginItem item = generatePluginItem(apkFile,classLoader);
                    plugins.add(item);

                    pluginPaths.add(item.getPluginPath());
                }
            }

            loadPluginResources(pluginPaths);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static PluginItem generatePluginItem(File apkFile, DexClassLoader classLoader) {
      return new PluginItem(apkFile.getPath(), DLUtils.getPackageInfo(mAppCtx, apkFile.getPath()),classLoader);
    }

    public static void loadPluginResources(String pluginPath){
        ArrayList<String> pluginPaths = new ArrayList<>();
        pluginPaths.add(pluginPath);
        loadPluginResources(pluginPaths);
    }

    private static void loadPluginResources(ArrayList<String> pluginPaths) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);


            addAssetPath.invoke(assetManager, mAppCtx.getPackageResourcePath());

            //添加插件的目录
            for (String pluginPath : pluginPaths) {
                addAssetPath.invoke(assetManager, pluginPath);
            }

            //生成新的Resources
            Resources newResources = new Resources(assetManager,
                    mAppCtx.getResources().getDisplayMetrics(),
                    mAppCtx.getResources().getConfiguration());
            //替换原来的Resources
            RefInvoke.setFieldObject(mAppCtx, "mResources", newResources);//支持插件运行时更新
            RefInvoke.setFieldObject(mPackageInfo, "mResources", newResources);

            mNowResources = newResources;
            //需要清理mTheme对象，否则通过inflate方式加载资源会报错
            //如果activity动态加载插件，则需要把activity的mTheme对象也设置为null
            RefInvoke.setFieldObject(mAppCtx, "mTheme", null);

//            mNowResources.newTheme().setTo(mAppCtx.getTheme());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    /**
     * 合并插件dex文件
     *
     * @param apkFile
     * @param optFile
     */
    public static void mergeDex(File apkFile, File optFile) {
        try {
            DexClassLoaderHelper.patchClassLoader(mAppCtx.getClassLoader(), apkFile, optFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 将assets目录下的apk读取到data/data/files目录下
     *
     * @param context
     * @param apkName
     * @throws IOException
     */
    public static void extractAssets(Context context, String apkName) {
        AssetManager am = context.getAssets();
        InputStream is = null;
        FileOutputStream fos = null;
        File extractFile = context.getFileStreamPath(apkName);
        try {

            is = am.open(apkName);
            fos = new FileOutputStream(extractFile);
            int len = -1;
            byte[] b = new byte[1024];
            while ((len = is.read(b)) != -1) {
                fos.write(b, 0, len);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(is);
            closeSilently(fos);
        }
    }

    private static void closeSilently(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Throwable e) {
            // ignore
        }
    }
}
