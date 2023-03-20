package com.kevin.juc.Thread.JUC;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class C {
    public static void main(String[] args) throws InterruptedException {
        extracted();


    }

    private static void extracted() {
       log.info("test1");
        log.info("test12");
    }

}

