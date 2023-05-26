package com.kevin.juc.time;

import lombok.AllArgsConstructor;

/**
 * 用于标记位置
 * @author liuyuteng
 */
@AllArgsConstructor
public enum IntervalPosition {


    TAIL(1,"尾部"),
    HEAD(2,"头部");

    public Integer value;

    public String desc;



}
