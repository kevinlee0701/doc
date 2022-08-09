package com.kevinlee.kafka;

import com.kevinlee.kafka.jicheng.KafkaCustomer;
import com.kevinlee.kafka.jicheng.KafkaProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
public class KafkaSpringbootTest {
    @Autowired
    private KafkaProducer kafkaProducer;
    @Autowired
    private KafkaCustomer kafkaCustomer;

    @Test
    public void test() throws InterruptedException {
        kafkaProducer.send();
        Thread.sleep(10000);
    }
}

