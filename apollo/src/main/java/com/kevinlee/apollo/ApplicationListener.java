package com.kevinlee.apollo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
@Order(value = Ordered.LOWEST_PRECEDENCE)
public class ApplicationListener implements SpringApplicationRunListener {

    private static final String APPID = "app.id";
    private static final String APPLICATION_RUNENV = "spring.profiles";
    private static final String APPLICATION_NAME = "spring.application.name";
    private static final String TEST="ceshi";


    public ApplicationListener(SpringApplication application, String[] args) {
        log.info("=======ApplicationListener");
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        log.info("=======ApplicationListener contextLoaded");
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        log.info("=======ApplicationListener contextPrepared");
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment env) {
        String appID = env.getProperty(APPID);
        String appName = env.getProperty(APPLICATION_NAME);
        String runEnv = env.getProperty(APPLICATION_RUNENV);
        String test = env.getProperty("ceshi");
        log.info("ApplicationListener:appID={},appName={},runEnv={},ceshi={}",appID,appName,runEnv,test);
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        log.info("=======ApplicationListener failed");
    }

    @Override
    public void running(ConfigurableApplicationContext context) {
        log.info("=======ApplicationListener running");
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        log.info("=======ApplicationListener started");
    }

}