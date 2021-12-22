package com.kevinlee.jvm;

/**
 * @ClassName TestStatic
 * @Author kevinlee
 * @Date 2021/12/22 11:21
 * @Version 1.0
 **/
public class TestStatic {
    public static void main(String[] args) {
        System.out.println(A.m);
    }
}
class A{
    static {
        m=300;
    }
    static int m =100;
}