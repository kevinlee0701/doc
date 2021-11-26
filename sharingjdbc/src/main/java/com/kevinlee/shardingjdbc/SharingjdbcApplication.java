package com.kevinlee.shardingjdbc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = {"com.kevinlee"})
@MapperScan("com.kevinlee.shardingjdbc.mapper")
public class SharingjdbcApplication {

    public static void main(String[] args) {
        SpringApplication.run(SharingjdbcApplication.class, args);
    }

}
