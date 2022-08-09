package com.kevinlee.kafka.jicheng;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 集成提供者
 */
@Component
public class KafkaProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void send(){
        System.out.println("send data");
        kafkaTemplate.send("first","kafka-springboot data");
    }
}
