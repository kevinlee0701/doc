package com.kevinlee.apollo.interceptor;

import com.kevinlee.apollo.interceptor.TestHandlerInterceptorAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @ClassName InterceptorConfig
 * @Author kevinlee
 * @Date 2022/3/31 14:56
 * @Version 1.0
 **/
@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TestHandlerInterceptorAdapter());
    }
}
