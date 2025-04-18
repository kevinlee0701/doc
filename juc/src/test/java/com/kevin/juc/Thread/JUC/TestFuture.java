package com.kevin.juc.Thread.JUC;

import com.kevin.juc.JucApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JucApplication.class)
@Slf4j
public class TestFuture {
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Test
    public void testTaskFuture() throws ExecutionException, InterruptedException {
        // 创建任务T2的FutureTask
        FutureTask<String> ft2 = new FutureTask(new T2Task());
        // 创建任务T1的FutureTask
        FutureTask<String> ft1 = new FutureTask(new T1Task(ft2));

        threadPoolTaskExecutor.submit(ft1);
        threadPoolTaskExecutor.submit(ft2);
//        // 线程T1执行任务ft1
//        Thread T1 = new Thread(ft1, "T1-Thread");
//        T1.start();
//        // 线程T2执行任务ft2
//        Thread T2 = new Thread(ft2, "T2-Thread");
//        T2.start();
        // 等待线程T1执行结果
        System.out.println(ft1.get());

    }

    @Test
    public void testCompletableFuture() {
        //任务1：洗水壶->烧开水
        CompletableFuture<Void> f1 =
                CompletableFuture.runAsync(() -> {

                    System.out.println("T1:洗水壶..."+Thread.currentThread().getName());
                    sleep(1, TimeUnit.SECONDS);

                    System.out.println("T1:烧开水..."+Thread.currentThread().getName());
                    sleep(10, TimeUnit.SECONDS);
                });

        //任务2：洗茶壶->洗茶杯->拿茶叶
        CompletableFuture<String> f2 =
                CompletableFuture.supplyAsync(() -> {
                    System.out.println("T2:洗茶壶..."+Thread.currentThread().getName());
                    sleep(1, TimeUnit.SECONDS);

                    System.out.println("T2:洗茶杯..."+Thread.currentThread().getName());
                    sleep(2, TimeUnit.SECONDS);

                    System.out.println("T2:拿茶叶..."+Thread.currentThread().getName());
                    sleep(1, TimeUnit.SECONDS);
                    return "龙井";
                });
        //任务3：任务1和任务2完成后执行：泡茶
        CompletableFuture<String> f3 =
                f1.thenCombine(f2, (__, tf) -> {
                    System.out.println("T1:拿到茶叶:" + tf);
                    System.out.println("T1:泡茶...");
                    return "上茶:" + tf;
                });
        //等待任务3执行结果
        System.out.println(f3.join());
    }

    public void sleep(int t, TimeUnit u) {
        try {
            u.sleep(t);
        } catch (InterruptedException e) {
        }
    }

    // T1Task需要执行的任务：
    // 洗水壶、烧开水、泡茶
    class T1Task implements Callable<String> {
        FutureTask<String> ft2;

        // T1任务需要T2任务的FutureTask
        T1Task(FutureTask<String> ft2) {
            this.ft2 = ft2;
        }

        @Override
        public String call() throws Exception {
            System.out.println("T1:洗水壶..."+Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(1);

            System.out.println("T1:烧开水..."+Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(2);
            // 获取T2线程的茶叶
            String tf = ft2.get();
            System.out.println("T1:拿到茶叶:" + tf+"===="+Thread.currentThread().getName());

            System.out.println("T1:泡茶...");
            return "上茶:" + tf;
        }
    }

    // T2Task需要执行的任务:
    // 洗茶壶、洗茶杯、拿茶叶
    class T2Task implements Callable<String> {
        @Override
        public String call() throws Exception {
            System.out.println("T2:洗茶壶..."+Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(1);

            System.out.println("T2:洗茶杯..."+Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(2);

            System.out.println("T2:拿茶叶..."+Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(1);
            return "龙井";

        }
    }

    //无返回值
    @Test
    public void runAsync() throws Exception {
        CompletableFuture future = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
            System.out.println("run end ...");
        });

        future.get();
    }

    //有返回值
    @Test
    public void supplyAsync() throws Exception {
        CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
            System.out.println("run end ...");
            return System.currentTimeMillis();
        });
        future.whenComplete((aLong, throwable) -> {
            log.info("whenComplete is running,result:{}", aLong);
        });
        long time = future.get();
        System.out.println("time = " + time);
    }
    @Test
    public void whenComplete() throws Exception {
        AtomicBoolean iswhenComplete= new AtomicBoolean(false);
        AtomicBoolean isExceptionally= new AtomicBoolean(false);
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
           return  12 / 0;
        });


        future.whenComplete(new BiConsumer<Integer, Throwable>() {
            @Override
            public void accept(Integer t, Throwable action) {
                System.out.println("执行完成！");
                iswhenComplete.set(true);
            }

        });

        future.exceptionally(new Function<Throwable, Integer>() {
            @Override
            public Integer apply(Throwable t) {
                System.out.println("执行失败！" + t.getMessage());
                isExceptionally.set(true);
                return null;
            }
        });
        System.out.println(iswhenComplete.get()+","+isExceptionally.get());
        while(!iswhenComplete.get() && !isExceptionally.get()){
            TimeUnit.SECONDS.sleep(2);
        }
    }
    @Test
    public void thenApply() throws Exception {
        CompletableFuture<Long> future = CompletableFuture.supplyAsync(new Supplier<Long>() {
            @Override
            public Long get() {
                long result = new Random().nextInt(100);
                System.out.println("result1="+result);
                return result;
            }
        });
        CompletableFuture<String> future1 = future.thenApply(new Function<Long, String>() {
            @Override
            public String apply(Long t) {
                long result = t * 5;
                System.out.println("result2=" + result);
                return String.valueOf(result);
            }
        });
        System.out.println(future1.get());
        System.out.println(future.get());
    }
    @Test
    public void thenCompose() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            return 100;
        });
        CompletableFuture<String> f = future.thenCompose( i -> {
            return CompletableFuture.supplyAsync(() -> {
                return (i * 10) + "";
            });
        });

       log.info(f.get());
    }

    @Test
    public void thenCombine(){
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "100");
        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> 100);

        CompletableFuture<Double> future = future1.thenCombine(future2, (s, i) -> Double.parseDouble(s + i));

        try {
            System.out.println(future.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runAfterBoth() throws InterruptedException {
        CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("s1 is done");
            return "s1";
        }).runAfterBoth(CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("s2 is done");
            return "s2";
        }), () -> log.info("last done"));  //() -> System.out.println("hello world")；

        Thread.sleep(10000);
    }

    @Test
    public void applyToEither(){
        Random random = new Random();

        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(()->{

            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("from future1");
            return "from future1";
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(()->{

            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("from future2");
            return "from future2";
        });

        CompletableFuture<String> future =  future1.applyToEither(future2, new Function<String,  String>() {
            @Override
            public String apply(String s) {
               log.info("s={}",s);
                return null;
            }
        });

        try {
            future.get();
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void acceptEither(){
        Random random = new Random();

        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(()->{

            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("from future1");
            return "from future1";
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(()->{

            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("from future2");
            return "from future2";
        });

        CompletableFuture<Void> future =  future1.acceptEither(future2,str->System.out.println("The future is "+str));

        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
