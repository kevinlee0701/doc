package com.kevinlee.book;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
@Slf4j
@Controller
public class BookController {


    @RequestMapping("/")
    public String index(){
        log.info("进入首页============");
        return "index";
    }

    @RequestMapping("/test")
    public String test(){
        return "test";
    }
}
