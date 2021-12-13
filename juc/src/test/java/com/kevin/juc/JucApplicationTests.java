package com.kevin.juc;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.IntStream;

@SpringBootTest
class JucApplicationTests {

    @Test
    void contextLoads() {
        List<Integer> list= Lists.newArrayList(1,2,3,4,5);
        Integer total=list.stream().reduce(3,(result,item)->result*item);
        System.out.println(total);//结果为：15
        list.parallelStream().reduce((a,b)->{return 1;});
    }

}
