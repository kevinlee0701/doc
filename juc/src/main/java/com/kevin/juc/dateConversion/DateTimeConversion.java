package com.kevin.juc.dateConversion;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeConversion {
    public static void main(String[] args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 将 LocalDateTime 转换为 Date
        LocalDateTime localDateTime = LocalDateTime.now();
        Date date = Date.from(localDateTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant());

        System.out.println("LocalDateTime: " + localDateTime.format(formatter));
        System.out.println("Date: " +sdf.format(date));

        // 将 Date 转换为 LocalDateTime
        Date currentDate = new Date();
        LocalDateTime convertedDateTime = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.of("Asia/Shanghai"));

        System.out.println("Date: " +sdf.format(currentDate) );
        System.out.println("LocalDateTime: " + convertedDateTime.format(formatter));
    }
}
