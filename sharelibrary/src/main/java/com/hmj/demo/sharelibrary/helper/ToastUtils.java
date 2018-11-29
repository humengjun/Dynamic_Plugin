package com.hmj.demo.sharelibrary.helper;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/9/1 0001.
 */

public class ToastUtils {

    private static Toast longToast;
    private static Toast shortToast;

    public static void shortToast(Context context, Object message) {
        if (shortToast == null) {
            synchronized (ToastUtils.class) {
                if (shortToast == null) {
                    shortToast = Toast.makeText(context.getApplicationContext(), message.toString(), Toast.LENGTH_SHORT);
                } else {
                    shortToast.setText(message.toString());
                }
            }
        } else {
            shortToast.setText(message.toString());
        }
        shortToast.show();
    }

    public static void longToast(Context context, String message) {
        if (longToast == null) {
            synchronized (ToastUtils.class) {
                if (longToast == null) {
                    longToast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG);
                }else {
                    longToast.setText(message);
                }
            }
        } else {
            longToast.setText(message);
        }
        longToast.show();
    }

}
