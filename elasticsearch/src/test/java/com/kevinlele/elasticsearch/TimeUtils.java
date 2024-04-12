package com.kevinlele.elasticsearch;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class TimeUtils {
    public static void main(String[] args) {
        LocalDate startDate = LocalDate.now(); // 设置起始日期
        int numberOfDaysToAdd = 10; // 要添加的天数

        LocalDate endDate = addBusinessDays(startDate, numberOfDaysToAdd);
        System.out.println("结束日期: " + endDate);
    }
   static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public static String addBusinessDays(Date startDate, int numberOfDaysToAdd){
        return sdf.format(toDate(addBusinessDays(toLocalDate(startDate),numberOfDaysToAdd)));
    }
    public static LocalDate addBusinessDays(LocalDate startDate, int numberOfDaysToAdd) {
        int addedDays = 0;
        LocalDate currentDate = startDate;

        while (addedDays < numberOfDaysToAdd) {
            currentDate = currentDate.plusDays(1);

            // 检查日期是否为工作日，如果不是则跳过
            if (isBusinessDay(currentDate)) {
                addedDays++;
            }
        }

        return currentDate;
    }
    static List<String> exclueList = Arrays.asList("2024-05-01","2024-05-02","2024-05-03","2024-05-04","2024-05-05");
    static  List<String> include = Arrays.asList("2024-04-28","2024-05-11");
    public static boolean isBusinessDay(LocalDate date) {
        // 这里简单地假设周六和周日为节假日
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        String format = sdf.format(toDate(date));
        if(include.contains(format)){
            return true;
        }
        if (exclueList.contains(format)){
            return false;
        }

        return !(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);
    }

    public static Date toDate(LocalDate localDate) {
        // 将 LocalDate 转换为 Date
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate toLocalDate(Date date) {
        // 将 Date 转换为 LocalDate
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
