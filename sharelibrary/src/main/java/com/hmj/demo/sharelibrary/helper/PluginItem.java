package com.hmj.demo.sharelibrary.helper;

import android.content.pm.PackageInfo;

import dalvik.system.DexClassLoader;

public class PluginItem {

    private String pluginPath;

    private PackageInfo pluginInfo;

    private DexClassLoader classLoader;

    public PluginItem(String pluginPath, PackageInfo pluginInfo, DexClassLoader classLoader) {
        this.pluginPath = pluginPath;
        this.pluginInfo = pluginInfo;
        this.classLoader = classLoader;
    }

    public DexClassLoader getClassLoader() {
        return classLoader;
    }

    public String getPluginPath() {
        return pluginPath;
    }

    public PackageInfo getPluginInfo() {
        return pluginInfo;
    }
}
