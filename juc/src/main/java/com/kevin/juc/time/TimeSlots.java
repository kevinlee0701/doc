package com.kevin.juc.time;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 类 描 述：时间类
 * 创建时间：2023/5/25 18:44
 * 创 建 人：lifeng
 */
@Data
public class TimeSlots implements Serializable,BaseTimeSlots{
    private static final int INTERVAL = 1;
    private static final int MILLISECOND_OF_ONE_MINUTE = 1000 * 60;
    private static final long serialVersionUID = -7254778829806640865L;

    private static final int MINUTES_OF_DAYS = 60*24;
    /**TimeSlots
     * 是否需要自动设置LessonTimeType属性
     */
    private  boolean isAutomaticSetLessonTimeType=true;

    /**
     * 老师ID
     *   private Integer teacherId;
     */


    /**
     * 日期
     */
    private Date arrangedDate;

    /**
     * 该时间段如果有课则具有课节id，由timeType控制
     */
    private Integer lessonId;

    /**
     * 该字段有两种含义
     * 1.课节类型 {@link }
     * 2.开放时间类型 {@link }
     */
    private Integer lessonTypeOrLessonTimeType;
    /**
     * 时间的类型   是开放（预占），可约/可排 ，有课，请假，请假审批中
     */
    private Integer timeType;

    /**
     * 时间的id，不同时间类型（TimeType）可能对应不用的表，所有id可能相同
     */
    private Integer timeId;

    /**
     * 时间槽，一旦在构造方法中设定，就不允许用setter进行更改
     */
    @Setter(AccessLevel.PRIVATE)
    private BitSet timeSlotContainer;

    public TimeSlots(Date arrangedDate, String timeSlots) {

        //this.teacherId = teacherId;
        this.arrangedDate = arrangedDate;

        this.timeSlotContainer = new BitSet();
        if (StringUtils.isNotBlank(timeSlots)) {
            for (int i = 0; i < timeSlots.length(); i++) {
                char c = timeSlots.charAt(i);
                timeSlotContainer.set(i, '1' == c);
            }
        }

    }
    public TimeSlots(Date startDate, Date endDate) {
        // this.teacherId = teacherId;
        this.arrangedDate = this.getDateStartTime(startDate);
        this.timeSlotContainer = new BitSet();
        this.occupy(startDate,endDate);
    }
    public static Date getDateStartTime(Date date) {
        Calendar ac = Calendar.getInstance();
        ac.setTime(date);
        ac.set(Calendar.HOUR_OF_DAY, 0);
        ac.set(Calendar.MINUTE, 0);
        ac.set(Calendar.SECOND, 0);
        ac.set(Calendar.MILLISECOND, 0);
        return ac.getTime();
    }

    /**
     * 转换为字符串表示
     * @return
     */
    public String toRaw() {
        StringBuilder timeSlotRaw = new StringBuilder();
        for (int i = 0; i < timeSlotContainer.length(); i++) {
            timeSlotRaw.append(timeSlotContainer.get(i) ? '1' : '0');
        }
        return timeSlotRaw.toString();
    }

    /**
     * 根据时间取得所在的SLOT
     *
     * @param date
     * @return
     */
    private int slot(Date date) {
        long minute = (date.getTime() - arrangedDate.getTime()) / MILLISECOND_OF_ONE_MINUTE;
        return (int) (minute / INTERVAL);
    }

    /**
     * 转换SLOT 为 时间表示
     *
     * @param start
     * @param end
     * @return
     */
    public TimeSlice render(int start, int end) {
        //返回null
        /*if (start==end){
            return null;
        }*/
        Date startTime = DateUtils.addMinutes(arrangedDate, start * INTERVAL);
        Date endTime = DateUtils.addMinutes(arrangedDate, (end + 1) * INTERVAL);
        //Date endTime = DateUtils.addMinutes(arrangedDate, end  * INTERVAL);
        TimeSlice timeSlice = new TimeSlice(startTime, endTime);
        timeSlice.setTimeType(this.timeType);
        timeSlice.setTimeId(this.timeId);
        timeSlice.setLessonType(this.lessonTypeOrLessonTimeType);
        timeSlice.setLessonId(this.lessonId);
        return timeSlice;
    }

    /**
     * 转换SLOT 为 时间表示
     * 手动设置 timeSlice 的lessonTimeType 属性
     * @param start
     * @param end
     * @return
     */
    public TimeSlice renderByManualSetLessonTimeType(int start, int end) {
        //返回null
        /*if (start==end){
            return null;
        }*/
        Date startTime = DateUtils.addMinutes(arrangedDate, start * INTERVAL);
        Date endTime = DateUtils.addMinutes(arrangedDate, (end + 1) * INTERVAL);
        //Date endTime = DateUtils.addMinutes(arrangedDate, end  * INTERVAL);
        TimeSlice timeSlice = new TimeSlice(startTime, endTime);
        timeSlice.setLessonTypeNotSetLessonTimeType(this.lessonTypeOrLessonTimeType);
        timeSlice.setLessonTimeType(this.lessonTypeOrLessonTimeType);
        timeSlice.setTimeType(this.timeType);
        timeSlice.setLessonId(this.lessonId);
        timeSlice.setTimeId(this.timeId);
        return timeSlice;
    }

    /**
     * 释放一个时间段
     *
     * @param slices
     * @return
     */
    public TimeSlots release(List<BaseTimeSlice> slices) {
        for (BaseTimeSlice slice : slices) {
            release(slice);
        }
        return this;
    }

    /**
     * 释放时间段
     *
     * @param slice
     * @return
     */
    public TimeSlots release(BaseTimeSlice slice) {
        if (slice==null){
            return this;
        }
        int startIndex = this.slot(slice.getStartTime());
        int endIndex = this.slot(slice.getEndTime());
        for (int i = startIndex; i < endIndex; i++) {
            timeSlotContainer.set(i, false);
        }
        return this;
    }

    /**
     * 释放时间段
     */
    public TimeSlots release(Date startTime,Date endTime) {
        int startIndex = this.slot(startTime);
        int endIndex = this.slot(endTime);
        for (int i = startIndex; i < endIndex; i++) {
            timeSlotContainer.set(i, false);
        }
        return this;
    }

    /**
     * 释放时间段
     */
    public TimeSlots release(TimeSlots timeSlots) {
        if (timeSlots!=null){
            String release = timeSlots.toRaw();
            release(release);
        }
        return this;
    }

    /**
     * 释放时间段
     * @param timeSlots
     * @return
     */
    public TimeSlots release(String timeSlots){
        if (StringUtils.isNotBlank(timeSlots)) {
            for (int i = 0; i < timeSlots.length(); i++) {
                char c = timeSlots.charAt(i);
                if ('1' == c){
                    timeSlotContainer.set(i, '1' != c);
                }

            }
        }
        return this ;
    }

    /**
     * 占用时间段
     *
     * @param slices
     * @return
     */
    public TimeSlots occupy(List<? extends BaseTimeSlice> slices) {
        for (BaseTimeSlice slice : slices) {
            occupy(slice);
        }
        return this;
    }

    /**
     * 占用一个时间段
     *
     * @param slice
     * @return
     */
    public TimeSlots occupy(BaseTimeSlice slice) {
        if (slice==null){
            return this;
        }
        int startIndex = this.slot(slice.getStartTime());
        int endIndex = this.slot(slice.getEndTime());
        for (int i = startIndex; i < endIndex; i++) {
            timeSlotContainer.set(i, true);
        }
        return this;
    }

    /**
     * 占用一个时间段
     *
     * @param timeSlots
     * @return
     */
    public TimeSlots occupy(TimeSlots timeSlots) {
        if (timeSlots==null){
            return this;
        }
        this.timeSlotContainer.or(timeSlots.getTimeSlotContainer());
        return this;
    }

    /**
     * 占用一个时间段
     *
     * @param timeSlots
     * @return
     */
    public TimeSlots occupy(String timeSlots) {
        if (StringUtils.isNotBlank(timeSlots)) {
            for (int i = 0; i < timeSlots.length(); i++) {
                char c = timeSlots.charAt(i);
                if ('1' == c){
                    timeSlotContainer.set(i, '1' == c);
                }

            }
        }
        return this;
    }

    /**
     * 占用一个时间段
     *
     * @param timeSlots
     * @return
     */
    public TimeSlots occupy(BaseTimeSlots timeSlots) {
        if (timeSlots==null){
            return this;
        }
        String timeSlotStr = timeSlots.getTimeSlots();
        occupy(timeSlotStr);
        return this;
    }
    /**
     * 占用一个时间段
     *
     * @return
     */
    public TimeSlots occupy(Date startTime,Date endTime) {

        if (startTime.getTime() >= endTime.getTime()) {
            throw new IllegalArgumentException("开始时间不能大于等于结束时间 startTime:" + startTime.getTime() + " 结束时间:" + endTime.getTime());
        }

        int startIndex = this.slot(startTime);
        int endIndex = this.slot(endTime);
        for (int i = startIndex; i < endIndex; i++) {
            timeSlotContainer.set(i, true);
        }
        return this;
    }
    /**
     * 清空该时间槽，即该时间槽表示的所有的时间均为未占用
     */
    public void clear(){
        this.timeSlotContainer.clear();
    }

    /**
     * 判断给timeslots是否为空
     * @return
     */
    public Boolean isEmpty(){
        return (this.timeSlotContainer.length() <=0);
    }

    /**
     * 已分配时间
     *
     * @return
     */
    public List<TimeSlice> occupiedTime() {
        List<TimeSlice> accupied = new ArrayList();
        int position = 0;
        int startIndex;
        int endIndex;
        while (true) {
            int nextSetBit = this.timeSlotContainer.nextSetBit(position);
            if (nextSetBit == -1) {
                break;
            }
            startIndex = nextSetBit;

            int nextClearBit = this.timeSlotContainer.nextClearBit(nextSetBit);

            if (nextClearBit == -1) {
                endIndex = startIndex;
            } else {
                endIndex = nextClearBit - 1;
            }
            position = this.timeSlotContainer.nextClearBit(nextSetBit);
            if(isAutomaticSetLessonTimeType){
                accupied.add(render(startIndex, endIndex));
            }else{
                accupied.add(renderByManualSetLessonTimeType(startIndex, endIndex));
            }
        }
        return accupied;
    }



    /**
     * 获取这个时间片段所占用的分钟数
     * @return
     */
    public int getOccupiedMinutes(){
        return this.timeSlotContainer.cardinality();
    }

    public String getTimeSlots() {
        return this.toRaw();
    }

    /**
     * 当前的bitset与传入的进行and匹配，如果完全匹配，返回true（
     *
     * 判断 传入的 timeslots是不是 this的父集
     *
     * @param timeSlots
     * @return 匹配返回true 不匹配返回 false
     */
    public Boolean matching(TimeSlots timeSlots){
        if (timeSlots==null){
            return false;
        }
        BitSet clone =(BitSet) this.timeSlotContainer.clone();
        int old= clone.cardinality();
        clone.and(timeSlots.getTimeSlotContainer());
        return clone.cardinality()==old;
    }


    /**
     * 检查两个timeslot是否有交集
     *
     * 当前的bitset与传入的进行and匹配，如果匹配完去重下标的个数为0，则返回true,用于判断时间是否交叉
     * @param timeSlots
     * @return true 有交集 false 无交际
     */
    public Boolean cross(TimeSlots timeSlots){
        if (timeSlots==null){
            return false;
        }
        BitSet clone =(BitSet) this.timeSlotContainer.clone();
        clone.and(timeSlots.getTimeSlotContainer());
        return clone.cardinality()!=0;
    }

    /**
     * 占用某一分钟时间
     * @param indexOfminutes 一天的第几分钟 0 - 60*24
     */
    public void occupy(Integer indexOfminutes){
        if (indexOfminutes >= MINUTES_OF_DAYS || indexOfminutes< 0 ){
            throw new RuntimeException("入参不合法：indexOfminutes："+indexOfminutes);
        }

        this.timeSlotContainer.set(indexOfminutes);
    }
}
