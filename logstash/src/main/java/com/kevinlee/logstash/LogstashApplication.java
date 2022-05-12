package com.kevinlee.logstash;


import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@Log4j2
@SpringBootApplication
public class LogstashApplication {

    public static void main(String[] args) {
        log.info("系统启动了");
        SpringApplication.run(LogstashApplication.class, args);
    }

}
