package com.kevinlee.kafka.jicheng;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 集成消费者
 */
@Component
public class KafkaCustomer {
    @KafkaListener(topics = {"first"})
    public void receive(String message){
        System.out.println("KafkaCustomer========first");
        System.out.println(message);
    }
}
