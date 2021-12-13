package com.kevin.juc.Thread.JUC;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName C
 * @Author kevinlee
 * @Date 2021/12/9 14:28
 * @Version 1.0
 **/
public class C {
    @Test
    public void test() throws InterruptedException {
        CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();
        for(int i=0 ;i<10; i++){
            list.add(i);
        }
        for(int i =1 ;i<6; i++){
            final int j = i;
            new Thread(()->{
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                list.remove(j);
                System.out.println(Thread.currentThread()+"删除："+j);
                },i+"--lee").start();

        }

        TimeUnit.SECONDS.sleep(10);

    }

}
