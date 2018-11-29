package com.hmj.demo.plugin_dynamic_demo.load_resource;

import dalvik.system.DexClassLoader;

public class PluginInfo {
    private String dexPath;
    private DexClassLoader classLoader;

    public PluginInfo(String dexPath, DexClassLoader classLoader) {
        this.dexPath = dexPath;
        this.classLoader = classLoader;
    }

    public String getDexPath() {
        return dexPath;
    }

    public DexClassLoader getClassLoader() {
        return classLoader;
    }
}
