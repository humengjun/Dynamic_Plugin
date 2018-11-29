package com.hmj.demo.plugin_dynamic_demo.hook;

import android.content.Context;

import com.hmj.demo.plugin_dynamic_demo.hook_broadcast_receiver.HookReceiverHelper;
import com.hmj.demo.plugin_dynamic_demo.hook_content_provider.HookProviderHelper;
import com.hmj.demo.plugin_dynamic_demo.hook_service.HookServiceHelper;

public class HookHelper {
    /**
     * 初始化Hook
     *
     * @param context
     * @throws Exception
     */
    public static void initHook(Context context) throws Exception {
        HookServiceHelper.initServiceHook();
        HookReceiverHelper.initReceiverHook(context);
        HookProviderHelper.initProviderHook(context);
    }
}
