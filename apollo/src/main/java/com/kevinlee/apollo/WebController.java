package com.kevinlee.apollo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * @ClassName WebController
 * @Author kevinlee
 * @Date 2022/3/25 15:32
 * @Version 1.0
 **/
@Controller
public class WebController {
    @Resource
    private SailfishProperties sailfishProperties;

    @RequestMapping("/test")
    public String test(){
        sailfishProperties.getAppName();
        return "index";
    }
}
