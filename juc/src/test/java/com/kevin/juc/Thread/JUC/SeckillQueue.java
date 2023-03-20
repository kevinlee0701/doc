package com.kevin.juc.Thread.JUC;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SeckillQueue {
    // 线程池，限制同时处理的请求数量
    private static ExecutorService executorService = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1000));

    // 消息队列，缓存请求
    private static ArrayBlockingQueue<SeckillRequest> queue = new ArrayBlockingQueue<>(1000);

    public static void addRequest(SeckillRequest request) {
        // 将请求添加到消息队列
        queue.offer(request);
    }

    public static void startSeckill() {
        while (true) {
            try {
                // 从消息队列中获取请求
                SeckillRequest request = queue.take();
                if(request == null){
                    System.out.println("队列没有数据，休眠 500毫秒");
                    Thread.sleep(500);
                }else{
                // 提交任务到线程池处理
                executorService.execute(new SeckillTask(request));

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

// 秒杀请求类
class SeckillRequest {
    private long userId;
    private long goodsId;

    public SeckillRequest(long userId, long goodsId) {
        this.userId = userId;
        this.goodsId = goodsId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

    @Override
    public String toString() {
        return "SeckillRequest{" +
                "userId=" + userId +
                ", goodsId=" + goodsId +
                '}';
    }
}

// 秒杀任务类
class SeckillTask implements Runnable {
    private SeckillRequest request;

    public SeckillTask(SeckillRequest request) {
        this.request = request;
    }

    @Override
    public void run() {
        if(request ==null){

        }
        // 处理秒杀请求
        // TODO: 实现秒杀逻辑
        System.out.println(Thread.currentThread().getName()+"==我开始买啦,处理中："+request.toString());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName()+"==买到啦");
    }
}
