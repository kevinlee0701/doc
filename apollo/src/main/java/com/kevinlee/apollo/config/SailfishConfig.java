package com.kevinlee.apollo.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName ApolloConfig
 * @Author kevinlee
 * @Date 2022/3/31 10:09
 * @Version 1.0
 **/
@Configuration
@EnableConfigurationProperties({SailfishProperties.class})
public class SailfishConfig {
}
