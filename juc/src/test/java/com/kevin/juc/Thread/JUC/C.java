package com.kevin.juc.Thread.JUC;

/**
 * @ClassName C
 * @Author kevinlee
 * @Date 2021/12/9 14:28
 * @Version 1.0
 **/
public class C {
    public static void main(String[] args) {
        String str = "world";
        switch (str) {
            case "hello":
                System.out.println("hello");
                break;
            case "world":
                System.out.println("world");
                break;
            default:
                break;
        }
    }

}
