package com.kevinlee.demo.Thread;

import com.kevinlee.demo.DemoApplication;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class TestSpringBoot {
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Test
    public void test(){
        threadPoolTaskExecutor.execute(() -> log.info("线程id={},name={}",Thread.currentThread().getId(),Thread.currentThread().getName()));
    }
}
