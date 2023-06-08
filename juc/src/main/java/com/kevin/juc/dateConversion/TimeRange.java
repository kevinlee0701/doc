package com.kevin.juc.dateConversion;

/**
 * 类 描 述：判断时间段连续
 * 创建时间：2023/6/8 17:17
 * 创 建 人：lifeng
 */

import java.time.LocalDateTime;

public class TimeRange {
    private LocalDateTime start;
    private LocalDateTime end;

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public TimeRange(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public boolean isContinuous(TimeRange other) {
        return this.end.isEqual(other.start) || this.start.isEqual(other.end);
    }

    public boolean isContinuous2(TimeRange other) {
        return this.end.isEqual(other.start) || this.start.isEqual(other.end) || (this.start.isBefore(other.end) && this.end.isAfter(other.start));
    }
    public static void main(String[] args) {
        LocalDateTime range1Start = LocalDateTime.of(2023, 6, 1, 8, 0);
        LocalDateTime range1End = LocalDateTime.of(2023, 6, 1, 12, 40);
        TimeRange range1 = new TimeRange(range1Start, range1End);

        LocalDateTime range2Start = LocalDateTime.of(2023, 6, 1, 12, 41);
        LocalDateTime range2End = LocalDateTime.of(2023, 6, 1, 16, 0);
        TimeRange range2 = new TimeRange(range2Start, range2End);

        LocalDateTime range3Start = LocalDateTime.of(2023, 6, 1, 17, 0);
        LocalDateTime range3End = LocalDateTime.of(2023, 6, 1, 19, 0);
        TimeRange range3 = new TimeRange(range3Start, range3End);

        LocalDateTime range4Start = LocalDateTime.of(2023, 6, 1, 19, 0);
        LocalDateTime range4End = LocalDateTime.of(2023, 6, 1, 23, 59);
        TimeRange range4 = new TimeRange(range4Start, range4End);

//        System.out.println("Range 1 and Range 2 are continuous: " + range1.isContinuous(range2));
//        System.out.println("Range 2 and Range 3 are continuous: " + range2.isContinuous(range3));
//        System.out.println("Range 3 and Range 4 are continuous: " + range3.isContinuous(range4));


        System.out.println("Range 1 and Range 2 are continuous2: " + range1.isContinuous2(range2));
        System.out.println("Range 2 and Range 1 are continuous2: " + range2.isContinuous2(range1));
    }
}
