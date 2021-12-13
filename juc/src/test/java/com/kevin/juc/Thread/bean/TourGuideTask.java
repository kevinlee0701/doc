package com.kevin.juc.Thread.bean;

import lombok.extern.slf4j.Slf4j;

/**
 * 导游线程，都到达目的地时，发放护照和签证
 *
 */
@Slf4j
public class TourGuideTask implements Runnable{

    @Override
    public void run() {
        log.info("****导游分发护照签证****");
        try {
            //模拟发护照签证需要2秒
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}