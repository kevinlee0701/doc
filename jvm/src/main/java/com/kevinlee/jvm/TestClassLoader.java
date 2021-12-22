package com.kevinlee.jvm;

/**
 * @ClassName TestClassLoader
 * @Author kevinlee
 * @Date 2021/12/22 11:35
 * @Version 1.0
 **/
public class TestClassLoader {

    public static void main(String[] args) throws ClassNotFoundException {
        //获取应用加载器
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader() ;
        System.out.println(systemClassLoader);
        //获取扩展类加载器
        ClassLoader parent = systemClassLoader.getParent();
        System.out.println(parent);
        //获取启动类加载器
        ClassLoader root = parent.getParent();
        System.out.println(root);
        //获取当前类是哪个加载器加载
        ClassLoader classLoader = Class.forName("com.kevinlee.jvm.TestClassLoader").getClassLoader();
        System.out.println(classLoader);
        //获取jdk内置类是哪个加载器加载
        ClassLoader jdkClassLoader = Class.forName("java.lang.Object").getClassLoader();
        System.out.println(jdkClassLoader);
    }
}
