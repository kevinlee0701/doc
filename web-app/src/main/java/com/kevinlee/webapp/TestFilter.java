package com.kevinlee.webapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * @ClassName TestFilter
 * @Author kevinlee
 * @Date 2022/3/25 17:08
 * @Version 1.0
 **/
@Slf4j
@Component
@WebFilter(filterName = "testFilter",urlPatterns = "/*")
public class TestFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        try{
            filterChain.doFilter(servletRequest,servletResponse);
        }catch (Exception e){
            log.error("TestFilter is error",e);
        }

    }
}
