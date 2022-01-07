package com.kevinlee.book;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
public class BookController {


    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @RequestMapping("/test")
    public String test(){
        return "test";
    }
}
