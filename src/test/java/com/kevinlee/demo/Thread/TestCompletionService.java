package com.kevinlee.demo.Thread;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Slf4j
public class TestCompletionService {


    public static void sleep(long time){
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
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

}
