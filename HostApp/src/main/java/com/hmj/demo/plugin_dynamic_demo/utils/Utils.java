package com.hmj.demo.plugin_dynamic_demo.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.hmj.demo.plugin_dynamic_demo.HostApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

    private static File sBaseDir;

    /**
     * 将assets目录下的apk读取到data/data/files目录下
     *
     * @param context
     * @param apkName
     * @throws IOException
     */
    public static void extractAssets(Context context, String apkName) throws IOException {
        AssetManager am = context.getAssets();
        InputStream is;
        FileOutputStream fos;
        File extractFile = context.getFileStreamPath(apkName);

        is = am.open(apkName);
        fos = new FileOutputStream(extractFile);
        int len = -1;
        byte[] b = new byte[1024];
        while ((len = is.read(b)) != -1) {
            fos.write(b, 0, len);
        }
        fos.flush();
        is.close();
        fos.close();
    }

    //待加载插件经过opt优化之后的路径
    public static File getPluginOptDexDir(String packageName) {
        return enforceDirExists(new File(getPluginBaseDir(packageName), "odex"));
    }

    //插件的lib库路径
    public static File getPluginLibDir(String packageName) {
        return enforceDirExists(new File(getPluginBaseDir(packageName), "lib"));
    }

    //加载插件的基本目录 /data/data/<package>/files/plugin
    private static File getPluginBaseDir(String packageName) {
        if (sBaseDir == null) {
            sBaseDir = HostApplication.getAppCtx().getFileStreamPath("plugin");
            enforceDirExists(sBaseDir);
        }
        return enforceDirExists(new File(sBaseDir, packageName));
    }

    private static synchronized File enforceDirExists(File file) {
        if (!file.exists()) {
            boolean ret = file.mkdir();
            if (!ret) {
                throw new RuntimeException("create dir" + file + "failed");
            }
        }
        return file;
    }


}
