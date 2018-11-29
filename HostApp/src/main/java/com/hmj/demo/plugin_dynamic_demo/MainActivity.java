package com.hmj.demo.plugin_dynamic_demo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hmj.demo.plugin_dynamic_demo.utils.PermissionUtils;
import com.hmj.demo.sharelibrary.IBean;
import com.hmj.demo.sharelibrary.helper.ToastUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class MainActivity extends Activity implements IBean.Listener {
    final String APK_NAME = "plugin1.apk";
    private DexClassLoader mClassLoader;
    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

    @BindView(R.id.edit)
    EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (PermissionUtils.checkPermission(this, permissions[0], 100)) {
            initClassLoaderFromSD();
        }
    }

    private void initClassLoaderFromSD() {
        File apkPath = new File(Environment.getExternalStorageDirectory(), "plugin_dynamic");
        File apk = new File(apkPath, APK_NAME);
        File fileRelease = getDir("dex", 0);

        Log.d("HostApp", "dexPath:" + apk.getAbsolutePath());
        Log.d("HostApp", "fileRelease:" + fileRelease.getAbsolutePath());
        mClassLoader = new DexClassLoader(apk.getAbsolutePath(), fileRelease.getAbsolutePath(), null, getClassLoader());
        try {
            BeanHelper.initBean((PathClassLoader) getClassLoader(), this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

//    private void initClassLoader() {
//        File extractFile = this.getFileStreamPath(APK_NAME);
//        String dexPath = extractFile.getPath();
//        File fileRelease = getDir("dex", 0);
//
//        Log.d("HostApp", "dexPath:" + dexPath);
//        Log.d("HostApp", "fileRelease:" + fileRelease.getAbsolutePath());
//
//        mClassLoader = new DexClassLoader(dexPath, fileRelease.getAbsolutePath(), null, getClassLoader());
//        try {
//            BeanHelper.initBean(mClassLoader, this);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

    @OnClick({R.id.getValue, R.id.setValue})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.getValue:
                Button button = (Button) view;
                button.setText(BeanHelper.getValue());
                break;
            case R.id.setValue:
                String value = edit.getText().toString();
                BeanHelper.setValue(value);
                break;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initClassLoaderFromSD();
            } else {
                ToastUtils.shortToast(this, "权限被拒绝！");
            }
        }
    }

    @Override
    public void onResult(String result) {
        Log.d("HostApp", result);
    }
}
