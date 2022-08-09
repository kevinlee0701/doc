package com.kevinlee.kafka.jicheng;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component //同样这里是必须的
public class KafkaCustomer {
    @KafkaListener(topics = {"first"})
    public void receive(String message){
        System.out.println("KafkaCustomer========first");
        System.out.println(message);
    }
}
