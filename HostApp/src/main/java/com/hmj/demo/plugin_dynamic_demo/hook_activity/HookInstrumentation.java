package com.hmj.demo.plugin_dynamic_demo.hook_activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.hmj.demo.plugin_dynamic_demo.utils.Reflection;

public class HookInstrumentation extends Instrumentation {
    Instrumentation mBase;

    public HookInstrumentation(Instrumentation mBase) {
        this.mBase = mBase;
    }

    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {
        Log.d("Instrumentation-HOOK", "Hook execStartActivity!");
        Class[] p1 = {Context.class, IBinder.class,
                IBinder.class, Activity.class,
                Intent.class, int.class, Bundle.class};
        Object[] v1 = {who, contextThread,
                token, target,
                intent, requestCode, options};
        return (ActivityResult) Reflection.invokeInstanceMethod(mBase, "execStartActivity", p1, v1);
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Log.d("Instrumentation-HOOK", "Hook newActivity!");

        //把替身恢复成真身
        Intent rawIntent = intent.getParcelableExtra("original_activity");
        if (rawIntent == null) {
            return mBase.newActivity(cl, className, intent);
        }

        String newClassName = rawIntent.getComponent().getClassName();
        return mBase.newActivity(cl, newClassName, rawIntent);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle bundle) {
        Log.d("Instrumentation-HOOK", "Hook callActivityOnCreate!");
        mBase.callActivityOnCreate(activity, bundle);
//        Class[] p1 = {Activity.class,Bundle.class};
//        Object[] v1 = {activity,bundle};
//        Reflection.invokeInstanceMethod(mBase,"callActivityOnCreate",p1,v1);
    }

}
