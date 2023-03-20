package com.kevin.juc.Thread;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kevin.juc.Thread.bean.JobAccountingBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 使用BlockingQueue 实现多线程处理流程
 * @author lifeng
 */
@Service("JobAccountingTest")
@Slf4j
public class JobAccountingTest {

    private static final int PAGE_SIZE = 50;

    private static final long keepAliveTime = 0L;

    private static final int max = Math.max(Runtime.getRuntime().availableProcessors() - 1, 1);

    private static final int child_max = 2 * max;

    private BlockingQueue<List<JobAccountingBo>> buffer = null;

    private ThreadPoolExecutor executor = null;

    private ThreadPoolExecutor childTaskExecutor = null;



    @PostConstruct
    private void init () {
        buffer = new ArrayBlockingQueue<List<JobAccountingBo>>(max);
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("job-accounting-thread-pool-%d").build();
        ThreadFactory childThreadFactory = new ThreadFactoryBuilder().setNameFormat("job-accounting-child-thread-pool-%d").build();
        executor = new ThreadPoolExecutor(max, max, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10000), threadFactory);
        childTaskExecutor = new ThreadPoolExecutor(child_max, child_max, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10000), childThreadFactory);
    }

    /**
     * 开始方法
     */
    public void computeJobAccounting(){
        AtomicBoolean finish = new AtomicBoolean(Boolean.FALSE);

        for (int i=0; i<max; i++) {
            //开启消费线程
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    computRun(finish);
                }
            });
        }

        String productLineIds = "1,2,3";
        final String[] split = productLineIds.split(",");
        final CountDownLatch countDownLatch = new CountDownLatch(split.length);

        //查询鲨鱼数据
        selectSharkData(countDownLatch, split);

        try {
//            countDownLatch.await(30,TimeUnit.MINUTES);
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("countDownLatch 异常",e);
        }

        finish.set(Boolean.TRUE);
        log.info("全局finish状态：{}",finish.get());

        while (buffer.size() != 0) {
            log.info("wait for sub...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        log.info("the main thread is finished ,finish status:{}!",finish.get());
    }

    /**
     * 查询鲨鱼数据
     */
    private void selectSharkData (CountDownLatch countDownLatch, String[] split) {

        //根据品线开启生产线程
        for (int i=0;i<split.length;i++) {
            final Integer productLineId = Integer.valueOf(split[i]);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getSharkSourceDate(productLineId, countDownLatch);
                }
            }).start();
        }
    }


    /**
     * 生产数据的方法
     * @param productLineId
     * @param countDownLatch
     */
    private void getSharkSourceDate (Integer productLineId, CountDownLatch countDownLatch) {
        try {
            List<JobAccountingBo> jobAccountingBos = Lists.newLinkedList();
            for(;;) {
                //分页查询，根据产品线ID查询
                List result = new ArrayList<>();
                //查询result数据，省略。。。。

                //判断容器为空时跳出循环
                if (CollectionUtils.isEmpty(result)) {
                    log.info("根据产品线分页查询，完成！！");
                    break;
                }
                //业务代码
                JobAccountingBo bean = new JobAccountingBo();

                jobAccountingBos.add(bean);
                }

                try {
                    buffer.put(jobAccountingBos);
                    log.info("put -- buffer size:{}",buffer.size());
                } catch (InterruptedException e) {
                    log.error("put BlockingQueue error",e);
                }
        } catch (Exception e) {
            log.error("程序异常", e);
        } finally {
            countDownLatch.countDown();
        }
    }

    /**
     * 消费方法
     * @param finish
     */
    private void computRun(AtomicBoolean finish) {

        String threadName = Thread.currentThread().getName();
        while(buffer.size() > 0 || !finish.get()) {

            //如果线程池 shutdown  take 会抛出异常终止该线程
            List<JobAccountingBo> subBuffer = null;
            try {
                subBuffer = buffer.poll(2000, TimeUnit.MILLISECONDS);
                //业务代码
                log.info("threadName:{},taking ...",threadName);
            } catch (InterruptedException e) {
                log.error("threadName:{},stop sub thread. write lt file.",threadName);
            }
        }
    }
}
