package com.kevinlee.webapp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName WebController
 * @Author kevinlee
 * @Date 2022/3/25 15:32
 * @Version 1.0
 **/
@Controller
public class WebController {
    @RequestMapping("/test")
    public String test(){
        return "test";
    }
}
