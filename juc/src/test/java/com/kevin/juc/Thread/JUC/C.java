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
       // extracted();
        StringBuilder sb = new StringBuilder();
        sb.append("第一行").append("\n");
        sb.append("第二行").append("\r\n");
        sb.append("第三行").append(System.lineSeparator()).append("第四行");
        System.out.println(sb.toString());



    }

    private static void extracted() {
       log.info("test1");
        log.info("test12");
    }

}

