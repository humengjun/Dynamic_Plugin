package com.hmj.demo.sharelibrary;

import android.app.Activity;
import android.content.res.Resources;

import com.hmj.demo.sharelibrary.helper.PluginHelper;

public class PluginBaseActivity extends Activity {

    @Override
    public Resources getResources() {
        return PluginHelper.mNowResources;
    }
}
