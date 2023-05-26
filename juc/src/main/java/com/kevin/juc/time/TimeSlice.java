package com.kevin.juc.time;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 教师开放的时间段
 * @author liuyuteng
 */
@Data
@NoArgsConstructor
public class TimeSlice implements Serializable,BaseTimeSlice{

	private static final long serialVersionUID = -7997129765196363137L;


	/**
	 * 该时间段如果有课则具有课节id，由timeType控制
	 */
	private Integer lessonId;
	/**
	 * 该段时间的类型  是开放（预占），可约/可排 ，有课，请假，请假审批中
	 */
	private Integer timeType;

	/**
	 * 开始时间
	 */
	private Date startTime;
	/**
	 * 结束时间
	 */
	private Date endTime;
	/**
	 * 课程类型 大班课，小班课，一对一高端，一对一预约
	 */
	private Integer lessonType;

	private Integer lessonTimeType;


	public void setLessonType(Integer lessonType){
		this.lessonType = lessonType;

	}

	public void setLessonTypeNotSetLessonTimeType(Integer lessonType){
		this.lessonType = lessonType;
	}

	public Integer getLessonTimeType(){
		if (lessonTimeType == null && lessonType != null){
			this.lessonTimeType =2;
		}
		return this.lessonTimeType;
	}

	/**
	 * 时间的id，不同时间类型（TimeType）可能对应不用的表，所有id可能相同
	 */
	private Integer timeId;

	public TimeSlice(Date startTime, Date endTime) {
		if (startTime.getTime() > endTime.getTime()) {
			throw new IllegalArgumentException("开始时间不能大于结束时间 startTime:" + startTime.getTime() + " 结束时间:" + endTime.getTime());
		}
		this.startTime = startTime;
		this.endTime = endTime;
	}

	/**
	 * 在指定的位置去掉一段时间，当timeInterval值大于当前时间范围则返回null
	 * @param timeInterval 分钟数
	 * @param position 位置
	 * @return 计算后的时间片
	 */
	public TimeSlice rmInterval(Integer timeInterval,Integer position){
		if (timeInterval == 0){
			return this;
		}

		Long millInterval =  timeInterval*60*1000L;
		long start = startTime.getTime();
		long end = endTime.getTime();

		if (IntervalPosition.HEAD.value.equals(position)){
			start = start + millInterval;
		}else if (IntervalPosition.TAIL.value.equals(position)){
			end = end - millInterval;
		}else {
			throw new IllegalArgumentException("position值不合法 ：" + position);
		}
		//当起始时间大于结束时间的时候就表示这个时间段不存在了
		if (start > end){
			return null;
		}

		startTime = new Date(start);

		endTime = new Date(end);

		return this;
	}

}
