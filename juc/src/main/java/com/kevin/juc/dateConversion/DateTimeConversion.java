package com.kevin.juc.dateConversion;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateTimeConversion {
    public static void main(String[] args) {
        // 将 LocalDateTime 转换为 Date
        LocalDateTime localDateTime = LocalDateTime.now();
        Date date = Date.from(localDateTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant());

        System.out.println("LocalDateTime: " + localDateTime);
        System.out.println("Date: " + date);

        // 将 Date 转换为 LocalDateTime
        Date currentDate = new Date();
        LocalDateTime convertedDateTime = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.of("Asia/Shanghai"));

        System.out.println("Date: " + currentDate);
        System.out.println("LocalDateTime: " + convertedDateTime);
    }
}
