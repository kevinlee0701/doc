package com.kevinlee.jvm;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName JVMTest
 * @Author kevinlee
 * @Date 2021/12/20 10:42
 * @Version 1.0
 **/
public class JVMTest {

    /**
     * @description: 用于生成dump文件
     * @Author kevinlee
     * @Date  2021/12/20
     **/
    @Test
    public void Test(){

        List list = new ArrayList<>();
        try{
        while(true){
            byte[] array = new byte[1*1024*1024];
            list.add(array);

        }
    }catch (Exception e){

        e.printStackTrace();

    }
    }
}
