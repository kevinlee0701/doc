package com.kevinlee.kafka.jicheng;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 集成提供者
 */
@Slf4j
@Component
public class KafkaProducer {


    @Autowired
    private KafkaTemplate kafkaTemplateK2;

    public void send(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String message = "kafka-springboot data ["+sdf.format(new Date())+"]";
        log.info("send data first :{}",message);
        kafkaTemplateK2.send("first",message);
    }
}
