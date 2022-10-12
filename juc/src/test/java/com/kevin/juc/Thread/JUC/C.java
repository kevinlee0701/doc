package com.kevin.juc.Thread.JUC;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @ClassName C
 * @Author kevinlee
 * @Date 2021/12/9 14:28
 * @Version 1.0
 **/
public class C {
    public static void main(String[] args) {
        ArrayList<Integer> list = Lists.newArrayList(1111111,1111111, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        TreeSet<Integer> collect = new TreeSet<>(list);
        System.out.println(new ArrayList<>(collect));
    }

}

