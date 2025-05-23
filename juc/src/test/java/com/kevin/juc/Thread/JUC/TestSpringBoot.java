package com.kevin.juc.Thread.JUC;

import com.kevin.juc.JucApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JucApplication.class)
public class TestSpringBoot {
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Test
    public void test(){
        threadPoolTaskExecutor.execute(() -> log.info("线程id={},name={}",Thread.currentThread().getId(),Thread.currentThread().getName()));
    }
}
