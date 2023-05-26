package com.kevin.juc.time;

import java.util.Date;

public interface BaseTimeSlice {

    /**
     * 获取时间段的开始时间
     * @return
     */
    Date getStartTime();

    /**
     * 获取时间段的结束时间
     * @return
     */
    Date getEndTime();
}