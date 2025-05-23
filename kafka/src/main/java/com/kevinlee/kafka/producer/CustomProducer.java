package com.kevinlee.kafka.producer;

import java.util.Properties;

import lombok.extern.log4j.Log4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 * kafka 发送端
 * @ClassName CustomProducer
 * @Author kevinlee
 * @Date 2022/7/6 15:02
 * @Version 1.0
 **/
@Log4j
public class CustomProducer {

    public static void main(String[] args) {

        // 0 配置
        Properties properties = new Properties();

        // 连接集群 bootstrap.servers
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"http://localhost:9092");

        // 指定对应的key和value的序列化类型 key.serializer
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());

        // 1 创建kafka生产者对象
        // "" hello
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);

        // 2 发送数据
        for (int i = 0; i < 1; i++) {
            log.info("发送数据："+i);
            kafkaProducer.send(new ProducerRecord<>("first","atguigu========="+i));
        }

        // 3 关闭资源
        kafkaProducer.close();
    }
}
