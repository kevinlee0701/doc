package com.kevinlee.demo.Thread;

import com.kevinlee.demo.Thread.bean.TourGuideTask;
import com.kevinlee.demo.Thread.bean.TravelTask;
import com.kevinlee.demo.Thread.bean.TravelTask2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.*;
@Slf4j
public class TestCyclicBarrier {
    private  Vector<Integer> orders = new Vector();
    private  Vector<Integer> transportation = new Vector();
    boolean isRemainOrder= true;//剩余订单
    boolean isRemainTransportation = true;//剩余对账单

    @Test
    void Test() throws InterruptedException {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3,new TourGuideTask());
        List<Callable<TravelTask>> listCall = new ArrayList<Callable<TravelTask>>();
        listCall.add(new TravelTask(cyclicBarrier,"哈登",5));
        listCall.add(new TravelTask(cyclicBarrier,"保罗",3));
        listCall.add(new TravelTask(cyclicBarrier,"戈登",1));
        ExecutorService executor = Executors.newFixedThreadPool(listCall.size());
        //登哥最大牌，到的最晚
        executor.invokeAll(listCall);
        executor.shutdown();
    }

    @Test
    void Test2() throws InterruptedException {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3+1,new TourGuideTask());
        ExecutorService executor = Executors.newFixedThreadPool(3);
        //登哥最大牌，到的最晚
        executor.execute(new TravelTask2(cyclicBarrier,"哈登",5));
        executor.execute(new TravelTask2(cyclicBarrier,"保罗",3));
        executor.execute(new TravelTask2(cyclicBarrier,"戈登",1));

        try {
            cyclicBarrier.await();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }finally {
            executor.shutdown();
        }
        log.info("主线程执行完毕");


    }


    @Test
    void Test3(){
        final CyclicBarrier barrier = new CyclicBarrier(3);
        producer(barrier);
        consumer(barrier);
    }

    @Test
    void Test4(){
        Executor executor =   Executors.newFixedThreadPool(1);

        final CyclicBarrier barrier = new CyclicBarrier(2,()->{
            executor.execute(()->consumer());
        });
        producer(barrier);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    void producer(CyclicBarrier barrier){
        log.info("开始生产");
        // 循环查询订单库
        Thread T1 = new Thread(()->{
            int i =0;
            while(isRemainOrder){
                // 查询订单库
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(i<3){
                    log.info("生产订单：{}",i);
                    orders.add(i);
                    i++;
                }else{
                    isRemainOrder=false;
                    log.info("订单生产完毕");
                }
                // 等待
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        });


        // 循环查询运单库
        Thread T2 = new Thread(()->{
            int i =0;
            while(isRemainTransportation){
                // 查询运单库
                if(i<3){
                    log.info("生产运单库：{}",i);
                    transportation.add(i);
                    i++;
                }else{
                    isRemainTransportation =false;
                    log.info("运单库生产完毕");
                }
                // 等待
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        });
        T1.start();
        T2.start();

    }
    void consumer(){
        Integer order = null;
        Integer tr = null;

        if(!orders.isEmpty())
            order = orders.remove(0);

        if(!transportation.isEmpty())
            tr = transportation.remove(0);
        // 执行对账操作
        if(order !=null && tr !=null)
            log.info("开始消费：order:{}:tr:{},isRemainTransportation:{},isRemainOrder:{}",order,tr,isRemainTransportation,isRemainOrder);
        log.info("消费完毕");
    }

    void consumer(CyclicBarrier cyclicBarrier){

        while ((!orders.isEmpty() || !transportation.isEmpty()) || isRemainTransportation || isRemainOrder){
            try {
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            Integer order = null;
            Integer tr = null;
            if(!orders.isEmpty()){
                order = orders.remove(0);
            }
            if(!transportation.isEmpty()) {
                tr = transportation.remove(0);
            }
            // 执行对账操作
            log.info("开始消费：order:{}:Transportation:{},isRemainTransportation:{},isRemainOrder:{}",order,tr,isRemainTransportation,isRemainOrder);
        }
        log.info("消费完毕");
    }
}
