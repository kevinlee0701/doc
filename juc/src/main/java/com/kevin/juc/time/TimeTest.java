package com.kevin.juc.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 类 描 述：时间类测试
 * 创建时间：2023/5/25 19:20
 * 创 建 人：lifeng
 */
public class TimeTest {
    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = sdf.parse("2023-06-01 03:00:00");
        Date endDate = sdf.parse("2023-06-01 05:00:00");
        TimeSlots timeSlots = new TimeSlots(startDate,endDate);
        Date startDate1 = sdf.parse("2023-06-01 03:50:00");
        Date endDate1 = sdf.parse("2023-06-01 04:05:00");
        TimeSlots timeSlots2 = new TimeSlots(startDate1,endDate1);

        Boolean cross = timeSlots2.cross(timeSlots);
        Boolean matching = timeSlots.matching(timeSlots2);


        TimeSlice timeSlice = new TimeSlice(startDate,endDate);
        timeSlice.rmInterval(5,IntervalPosition.HEAD.value);
    }
}
