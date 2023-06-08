package com.kevin.juc.dateConversion;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TimeRangeMerger {


    /**
     * 合并时间，时间交叉认为 不连续 eg：8：00-10:00 与9：00—12：00 结果为：8：00-10:00 ，9：00—12：00
     * @param ranges
     * @return
     */
    public static List<TimeRange> merge(List<TimeRange> ranges) {
        if (ranges == null || ranges.isEmpty()) {
            return new ArrayList<>();
        }

        // 根据时间段的开始时间进行排序
        ranges.sort(Comparator.comparing(TimeRange::getStart));

        List<TimeRange> mergedRanges = new ArrayList<>();
        TimeRange currentRange = ranges.get(0);

        for (int i = 1; i < ranges.size(); i++) {
            TimeRange nextRange = ranges.get(i);
            //结束时间与开始时间不相等认为不连续【时间交叉认为是不连续】
            if (!currentRange.getEnd().isEqual(nextRange.getStart())) {
                mergedRanges.add(currentRange);
                currentRange = nextRange;
            } else {
                // 当前时间段与下一个时间段连续，更新当前时间段的结束时间
                currentRange = new TimeRange(currentRange.getStart(), nextRange.getEnd());
            }
        }

        // 添加最后一个时间段
        mergedRanges.add(currentRange);
        return mergedRanges;
    }

    /**
     * 合并时间，时间交叉认为 连续 eg：8：00-10:00 与9：00—12：00 结果为：8：00—12：00
     * @param ranges
     * @return
     */
    public static List<TimeRange> merge2(List<TimeRange> ranges) {
        if (ranges == null || ranges.isEmpty()) {
            return new ArrayList<>();
        }

        // 根据时间段的开始时间进行排序
        ranges.sort(Comparator.comparing(TimeRange::getStart));

        List<TimeRange> mergedRanges = new ArrayList<>();
        TimeRange currentRange = ranges.get(0);

        for (int i = 1; i < ranges.size(); i++) {
            TimeRange nextRange = ranges.get(i);

            if (currentRange.getEnd().isBefore(nextRange.getStart())) {
                // 当前时间段与下一个时间段不连续，将当前时间段加入合并结果
                mergedRanges.add(currentRange);
                currentRange = nextRange;
            } else {
                // 当前时间段与下一个时间段连续，更新当前时间段的结束时间
                currentRange = new TimeRange(currentRange.getStart(), nextRange.getEnd());
            }
        }

        // 添加最后一个时间段
        mergedRanges.add(currentRange);

        return mergedRanges;
    }

    public static void main(String[] args) {
        LocalDateTime range1Start = LocalDateTime.of(2023, 6, 1, 8, 0);
        LocalDateTime range1End = LocalDateTime.of(2023, 6, 1, 12, 0);
        TimeRange range1 = new TimeRange(range1Start, range1End);

        LocalDateTime range2Start = LocalDateTime.of(2023, 6, 1, 12, 0);
        LocalDateTime range2End = LocalDateTime.of(2023, 6, 1, 16, 0);
        TimeRange range2 = new TimeRange(range2Start, range2End);

        LocalDateTime range3Start = LocalDateTime.of(2023, 6, 1, 16, 0);
        LocalDateTime range3End = LocalDateTime.of(2023, 6, 1, 21, 0);
        TimeRange range3 = new TimeRange(range3Start, range3End);

        LocalDateTime range4Start = LocalDateTime.of(2023, 6, 1, 22, 0);
        LocalDateTime range4End = LocalDateTime.of(2023, 6, 1, 23, 59);
        TimeRange range4 = new TimeRange(range4Start, range4End);

        List<TimeRange> ranges = new ArrayList<>();
        ranges.add(range4);
        ranges.add(range2);
        ranges.add(range1);
        ranges.add(range3);

        List<TimeRange> mergedRanges = merge(ranges);

        System.out.println("Merged Ranges:");
        for (TimeRange range : mergedRanges) {
            System.out.println(range.getStart() + " - " + range.getEnd());
        }
    }
}

