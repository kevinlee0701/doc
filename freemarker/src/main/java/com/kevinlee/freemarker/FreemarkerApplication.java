package com.kevinlee.freemarker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.kevinlee"})
public class FreemarkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FreemarkerApplication.class, args);
    }

}
