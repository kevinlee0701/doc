package com.kevinlee;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName Test
 * @Author kevinlee
 * @Date 2022/1/10 18:21
 * @Version 1.0
 **/
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class Test {


    @org.junit.Test
    public void addCache(){
        RedisBean user = new RedisBean();
        user.setId(1);
        user.setName("测试中文");
        user.setList(Lists.newCopyOnWriteArrayList());

        CacheUtil.addCache("beantest",user);
    }
    @org.junit.Test
    public void getCache(){
        RedisBean beantest = CacheUtil.getCache("beantest", RedisBean.class);
        System.out.println(beantest);
    }

    @org.junit.Test
    public void delCache(){
       CacheUtil.delCache("beantest");
    }
}
