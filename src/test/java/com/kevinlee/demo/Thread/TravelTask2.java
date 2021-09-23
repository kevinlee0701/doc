package com.kevinlee.demo.Thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;

/**
 * 旅行线程
 *
 */
@Slf4j
public class TravelTask implements Callable {

    private CyclicBarrier cyclicBarrier;
    private String name;
    private int arriveTime;//赶到的时间

    public TravelTask(CyclicBarrier cyclicBarrier,String name,int arriveTime){
        this.cyclicBarrier = cyclicBarrier;
        this.name = name;
        this.arriveTime = arriveTime;
    }

    @Override
    public Object call() throws Exception {
        try {
            //模拟达到需要花的时间
            Thread.sleep(arriveTime * 1000);
            log.info(name +"到达集合点");
            cyclicBarrier.await();
            log.info(name +"开始旅行啦～～");
        } catch (InterruptedException e) {
            log.error("run is error",e);
        } catch (BrokenBarrierException e) {
            log.error("run is error",e);
        }
        return null;
    }
}