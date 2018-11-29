package com.hmj.demo.sharelibrary.helper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

public class DexClassLoaderHelper {


    /**
     * 增加插件的classloader
     * @param cl
     * @param apkFile
     * @param optFile
     * @throws IOException
     */
    public static void patchClassLoader(ClassLoader cl, File apkFile, File optFile) throws IOException {
        //获取BaseDexClassLoader：pathList
        Object pathListObj = RefInvoke.getFieldObject(DexClassLoader.class.getSuperclass(), cl, "pathList");
        //获取PathList：Element[]dexElements
        Object[] dexElements = (Object[]) RefInvoke.getFieldObject(pathListObj, "dexElements");
        //获取Element类型
        Class<?> elementClass = dexElements.getClass().getComponentType();
        //创建数组，替换原来的数组
        Object[] newElements = (Object[]) Array.newInstance(elementClass, dexElements.length + 1);
        //构造插件Element(File file,boolean isDirectory,File zip,DexFile dexFile)
        Class[] p1 = {File.class, boolean.class, File.class, DexFile.class};
        DexFile dexFile = DexFile.loadDex(apkFile.getCanonicalPath(), optFile.getAbsolutePath(), 0);
        Object[] v1 = {apkFile, false, apkFile, dexFile};
        Object o = RefInvoke.createObject(elementClass, p1, v1);

        Object[] toAddElementArray = new Object[]{o};
        //把原始的elements复制进去
        System.arraycopy(dexElements, 0, newElements, 0, dexElements.length);
        //复制插件的element
        System.arraycopy(toAddElementArray, 0, newElements, dexElements.length, toAddElementArray.length);

        //替换
        RefInvoke.setFieldObject(pathListObj, "dexElements", newElements);

    }
}
