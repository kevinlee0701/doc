package com.kevin.juc.Thread;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 资料：https://www.cnblogs.com/myseries/p/12634557.html
 *
 * RateLimiter是Guava的一个限流组件，使用的是一种叫令牌桶的流控算法，RateLimiter会按照一定的频率往桶里扔令牌，线程拿到令牌才能执行，
 * 比如你希望自己的应用程序QPS不要超过1000，那么RateLimiter设置1000的速率后，就会每秒往桶里扔1000个令牌
 *
 * 修饰符和类型	方法和描述
 * double	acquire()
 * 从RateLimiter获取一个许可，该方法会被阻塞直到获取到请求
 *
 * double	acquire(int permits)
 * 从RateLimiter获取指定许可数，该方法会被阻塞直到获取到请求
 *
 * static RateLimiter	create(double permitsPerSecond)
 * 根据指定的稳定吞吐率创建RateLimiter，这里的吞吐率是指每秒多少许可数（通常是指QPS，每秒多少查询）
 *
 * static RateLimiter	create(double permitsPerSecond, long warmupPeriod, TimeUnit unit)
 * 根据指定的稳定吞吐率和预热期来创建RateLimiter，这里的吞吐率是指每秒多少许可数（通常是指QPS，每秒多少个请求量），在这段预热时间内，RateLimiter每秒分配的许可数会平稳地增长直到预热期结束时达到其最大速率。（只要存在足够请求数来使其饱和）
 *
 * double	getRate()
 * 返回RateLimiter 配置中的稳定速率，该速率单位是每秒多少许可数
 *
 * void	setRate(double permitsPerSecond)
 * 更新RateLimite的稳定速率，参数permitsPerSecond 由构造RateLimiter的工厂方法提供。
 *
 * String	toString()
 * 返回对象的字符表现形式
 *
 * boolean	tryAcquire()
 * 从RateLimiter 获取许可，如果该许可可以在无延迟下的情况下立即获取得到的话
 *
 * boolean	tryAcquire(int permits)
 * 从RateLimiter 获取许可数，如果该许可数可以在无延迟下的情况下立即获取得到的话
 *
 * boolean	tryAcquire(int permits, long timeout, TimeUnit unit)
 * 从RateLimiter 获取指定许可数如果该许可数可以在不超过timeout的时间内获取得到的话，或者如果无法在timeout 过期之前获取得到许可数的话，那么立即返回false （无需等待）
 *
 * boolean	tryAcquire(long timeout, TimeUnit unit)
 * 从RateLimiter 获取许可如果该许可可以在不超过timeout的时间内获取得到的话，或者如果无法在timeout 过期之前获取得到许可的话，那么立即返回false（无需等待）
 */
@Slf4j
public class RateLimiterTest {

    @Test
    public void test1() throws InterruptedException {
        //线程池
        ExecutorService exec = Executors.newCachedThreadPool();
        //速率是每秒只有3个许可
        final RateLimiter rateLimiter = RateLimiter.create(1.0); //速率是每秒只有3个许可

        for (int i = 0; i < 10; i++) {
            final int no = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        rateLimiter.acquire(3);//拿取令牌个数3
                        System.out.println("Accessing: " + no + ",time:"
                                + new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date()));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
            //执行线程
            exec.execute(runnable);
        }
        Thread.sleep(30000);
        //退出线程池
        exec.shutdown();
    }

    public static void main(String[] args) {
        final CountDownLatch latch = new CountDownLatch(1);

        for (int i = 0; i < 7; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        latch.await();
                        Thread.sleep(1000);
                        System.out.println("请求是否被执行: "+System.currentTimeMillis());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }

        latch.countDown();
    }


}
