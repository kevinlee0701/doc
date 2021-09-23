package com.kevinlee.demo.Thread;

import com.kevinlee.demo.Thread.bean.TourGuideTask;
import com.kevinlee.demo.Thread.bean.TravelTask;
import com.kevinlee.demo.Thread.bean.TravelTask2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
@Slf4j
public class TestCyclicBarrier {

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
}
