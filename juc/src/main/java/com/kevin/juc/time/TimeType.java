package com.kevin.juc.time;



/**
 * @author liuyuteng
 * @date 2019/6/26
 */
public enum TimeType implements CommonEnumSkeleton {

    /**
     * 对时间类型进行标识
     */
    PREEMPT_TIME("预占时间", 1), CAN_BE_USED("可约可排", 2), LESSON_EXIST("有课", 3), LEAVE("请假", 4),LEAVE_APPROVING("请假审批中",5);
    private String name;
    private Integer value;

    private TimeType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    @Override
    public int getValue() {
        return value;
    }


    public Integer getIntegerValue() {
        return value;
    }

    public static TimeType get(Integer value) {
        for (TimeType e : values()) {
            if (e.value .equals( value) ) {
                return e;
            }
        }
        return null;
    }

}
