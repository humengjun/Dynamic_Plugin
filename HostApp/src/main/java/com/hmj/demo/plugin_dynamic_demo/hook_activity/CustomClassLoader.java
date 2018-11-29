package com.hmj.demo.plugin_dynamic_demo.hook_activity;

import dalvik.system.DexClassLoader;

public class CustomClassLoader extends DexClassLoader {

    public CustomClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
    }
}
