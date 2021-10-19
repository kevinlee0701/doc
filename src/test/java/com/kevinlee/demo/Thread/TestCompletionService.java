package com.kevinlee.demo.Thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * CompletionService是JDK8中引入的的接口，目的是解决Future的缺点，因为在获取提交给定义ExecutorService线程池的批量任务结果时的返回值Future的get()方法是阻塞的，
 * 一旦前一个任务执行比较耗时，后续的任务调用get()方法就需要阻塞，从而形成排队等待的情况。而CompletionService是对定义ExecutorService进行了包装，把任务提交到一个队列中，
 * 先完成的任务结果，先保存到一个阻塞队列中，通过CompletionService的take()方法可以获取最先完成的任务结果。
 */
@Slf4j
public class TestCompletionService {


    public static void sleep(long time){
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 方法poll的作用是获取并移除表示下一个已完成任务的Future，如果不存在这样的任务，则返回null，方法poll是无阻塞的。
     */
    @Test
    void testPoll() {
        //定义ExecutorService
        ExecutorService executor = Executors.newCachedThreadPool();
        //定义批量任务，每个任务的耗时不等
        final List<Callable<Integer>> tasks = Arrays.asList(
                () -> {
                    sleep(30L);
                    System.out.println("Task 30 completed done.");
                    return 30;
                },
                () -> {
                    sleep(10L);
                    System.out.println("Task 10 completed done.");
                    return 10;
                },
                () -> {
                    sleep(20L);
                    System.out.println("Task 20 completed done.");
                    return 20;
                }
        );

        //批量提交执行异步任务，
        try {
            CompletionService<Integer> completionService = new ExecutorCompletionService<>(executor);
            tasks.forEach(completionService::submit);
            for (int i = 0; i < tasks.size(); i++){
                try {
                    Future<Integer> poll = completionService.poll(30, TimeUnit.SECONDS);
                    Integer result = null;
                    if(poll !=null){
                        result = poll.get();
                    }
                    System.out.println("返回结果： " +result);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            executor.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * take()方法取得最先完成任务的Future对象，谁执行时间最短谁最先返回
     */
    @Test
    void testTake() {
        //定义ExecutorService
        ExecutorService executor = Executors.newCachedThreadPool();
        //定义批量任务，每个任务的耗时不等
        final List<Callable<Integer>> tasks = Arrays.asList(
                () -> {
                    sleep(30L);
                    System.out.println("Task 30 completed done.");
                    return 30;
                },
                () -> {
                    sleep(10L);
                    System.out.println("Task 10 completed done.");
                    return 10;
                },
                () -> {
                    sleep(20L);
                    System.out.println("Task 20 completed done.");
                    return 20;
                }
        );

        //批量提交执行异步任务，
        try {
            CompletionService<Integer> completionService = new ExecutorCompletionService<>(executor);
            tasks.forEach(completionService::submit);
            for (int i = 0; i < tasks.size(); i++){
                try {
                    System.out.println("返回结果： " + completionService.take().get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            executor.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void testThread() throws InterruptedException {
        ThreadFactory childThreadFactory = new ThreadFactoryBuilder().setNameFormat("lifeng-%d").build();
        ExecutorService executorService = Executors.newFixedThreadPool(3,childThreadFactory);
        for(int i =0 ; i<100 ;i++){
            int finalI = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("i="+ finalI+",Thread"+Thread.currentThread());
                }
            });
        }
        Thread.sleep(100000);
        executorService.shutdown();

    }



    @Test
    void testThread2() throws InterruptedException, ExecutionException {
        Long start = System.currentTimeMillis();
        //开启3个线程
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        try {
            int taskCount = 10;
            // 结果集
            List<Integer> list = new ArrayList<Integer>();


            // 1.定义CompletionService
            CompletionService<Integer> completionService = new ExecutorCompletionService<Integer>(executorService);

            // 2.添加任务
            for(int i=0;i<taskCount;i++){
               completionService.submit(new Task(i+1));
            }

            // 3.获取结果
            for(int i=0;i<taskCount;i++){
                Integer result = completionService.take().get();
                System.out.println("任务i=="+result+"完成!"+System.currentTimeMillis()/1000);
                list.add(result);
            }

            System.out.println("list="+list);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭线程池
            executorService.shutdown();
        }
    }
    static class Task implements Callable<Integer>{
        Integer i;

        public Task(Integer i) {
            super();
            this.i=i;
        }

        @Override
        public Integer call() throws Exception {
            Thread.sleep(5000);
            System.out.println("线程："+Thread.currentThread().getName()+"任务i="+i+",执行完成！");
            return i;
        }

    }
}


