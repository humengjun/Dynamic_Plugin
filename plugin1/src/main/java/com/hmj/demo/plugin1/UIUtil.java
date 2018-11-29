package com.hmj.demo.plugin1;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

public class UIUtil {
    public static String getString(Context context) {
        return context.getString(R.string.app_name);
    }

    public static Drawable getDrawable(Context context) {
        return context.getDrawable(R.mipmap.sensor);
    }

    public static View getLayout(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_plugin, null);
        Log.d("getLayout", (view == null) + "");
        return view;
    }


}
