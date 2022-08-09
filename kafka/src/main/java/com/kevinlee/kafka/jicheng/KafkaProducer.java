package com.kevinlee.kafka.jicheng;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component //这个必须加入容器不然，不会执行
public class KafkaProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void send(){
        System.out.println("send data");
        kafkaTemplate.send("first","kafka-springboot data");
        //发送方式很多种可以自己研究一下
    }
}
