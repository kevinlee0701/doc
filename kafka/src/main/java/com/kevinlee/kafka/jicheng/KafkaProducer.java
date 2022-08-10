package com.kevinlee.kafka.jicheng;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 集成提供者
 */
@Slf4j
@Component
public class KafkaProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void send(){
        log.info("send data");
        kafkaTemplate.send("first","kafka-springboot data");
    }
}
