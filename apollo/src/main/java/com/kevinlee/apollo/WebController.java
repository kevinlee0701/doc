package com.kevinlee.apollo;

import com.kevinlee.apollo.config.SailfishProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * @ClassName WebController
 * @Author kevinlee
 * @Date 2022/3/25 15:32
 * @Version 1.0
 **/
@Slf4j
@Controller
public class WebController {
    @Resource
    private SailfishProperties sailfishProperties;

    @RequestMapping("/test")
    public String test(){
        String appName = sailfishProperties.getAppName();
        log.info("http://localhost:8888/test,appName={}",appName);
        return "index";
    }
}
