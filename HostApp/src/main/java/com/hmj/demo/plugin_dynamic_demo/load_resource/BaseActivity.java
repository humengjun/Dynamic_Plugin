package com.hmj.demo.plugin_dynamic_demo.load_resource;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;

import com.hmj.demo.sharelibrary.helper.PluginHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BaseActivity extends Activity {

    private Resources mResources;
    private AssetManager mAssetManager;
    private Resources.Theme mTheme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        mResources = PluginHelper.mNowResources;
    }

    public void loadResource(String dexPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);

            //这行代码必须放在attachBaseContext方法内才有效
//            addAssetPath.invoke(assetManager, getPackageResourcePath());
            addAssetPath.invoke(assetManager, dexPath);
            mAssetManager = assetManager;

            Resources superRes = super.getResources();
            mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());

//            Object mPackageInfo = RefInvoke.getFieldObject(this.getApplicationContext(), "mPackageInfo");
//            RefInvoke.setFieldObject(this.getApplicationContext(), "mResources", mResources);//支持插件运行时更新
//            RefInvoke.setFieldObject(mPackageInfo, "mResources", mResources);
//
//            RefInvoke.setFieldObject(this, "mTheme", null);

            mTheme = mResources.newTheme();
            mTheme.setTo(super.getTheme());

            PluginHelper.mNowResources = mResources;

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AssetManager getAssets() {
        return mAssetManager == null ? super.getAssets() : mAssetManager;
    }

    @Override
    public Resources getResources() {
        return mResources == null ? super.getResources() : mResources;
    }

    @Override
    public Resources.Theme getTheme() {
        return mTheme == null ? super.getTheme() : mTheme;
    }
}
