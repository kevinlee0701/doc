package com.kevinlee.kafka.jicheng;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 集成消费者
 */
@Slf4j
@Component
public class KafkaCustomer {
    @KafkaListener(topics = {"first"},containerFactory = "kafkaListenerContainerFactoryK2" )
    public void receive(String message){
        log.info("KafkaCustomer========first,message={}",message);
    }
}
