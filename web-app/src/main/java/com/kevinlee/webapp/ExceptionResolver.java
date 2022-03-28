package com.kevinlee.webapp;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName ExceptionResolver
 * @Author kevinlee
 * @Date 2022/3/25 17:11
 * @Version 1.0
 **/
@Slf4j
@Component
public class ExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        log.info("ExceptionResolver in");
        response.setContentType("application/json;charset=UTF-8");
        try {
           PrintWriter writer = response.getWriter();

           Map map = new HashMap<>();
           map.put("message","ExceptionResolver is coming");
           writer.write(JSON.toJSONString(map));
           writer.flush();
           writer.close();
        } catch (IOException e) {
            log.error("resolveException is error");
        }
        return null;
    }
}
