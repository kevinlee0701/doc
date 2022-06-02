package com.kevinlee.logstash;


import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@Log4j2
@SpringBootApplication
public class LogstashApplication {

    public static void main(String[] args) {
        log.info("系统启动了");
        Logger log123 = LoggerFactory.getLogger("APMInfoDev");
        log123.info("测试一下");
        SpringApplication.run(LogstashApplication.class, args);
    }

}
