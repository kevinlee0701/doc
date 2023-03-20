package com.kevin.juc.Thread.JUC;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * 类 描 述：测试SeckillQueue
 * 创建时间：2023/3/16 14:17
 * 创 建 人：lifeng
 */
@Slf4j
public class TestQueue {

    @Test
    public void Test() throws InterruptedException {


        for (int i = 0 ; i<10 ;i++) {
            SeckillRequest request = new SeckillRequest(i,i);
            SeckillQueue.addRequest(request);
        }
        SeckillQueue.startSeckill();

    }
}
