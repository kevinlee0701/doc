package com.kevin.juc;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * CopyOnWriteListTest
 * @Author kevinlee
 * @Date 2021/12/24 17:19
 * @Version 1.0
 **/
public class CopyOnWriteListTest {
    public static void main(String[] args) {
        List list = new CopyOnWriteArrayList<>();
        for(int i = 0 ; i<10000;i++){
            list.add(i);
        }
        Thread thread = new Thread(() -> {
            Iterator<Integer> iter = list.iterator();
            while (iter.hasNext()) {
                Integer item = iter.next();
                System.out.println(item + " " +Thread.currentThread().getName());
                list.remove(item);
            }
        });

        Thread thread2 = new Thread(() -> {
            Iterator<Integer> iter = list.iterator();
            while (iter.hasNext()) {
                Integer item = iter.next();
                System.out.println(item + " " +Thread.currentThread().getName());
                list.remove(item);
            }
        });

        Thread thread3= new Thread(() -> {
            Iterator<Integer> iter = list.iterator();
            while (iter.hasNext()) {
                Integer item = iter.next();
                System.out.println(item + " " +Thread.currentThread().getName());
                list.remove(item);
            }
        });

        thread.start();
        thread2.start();
        thread3.start();

    }
}
