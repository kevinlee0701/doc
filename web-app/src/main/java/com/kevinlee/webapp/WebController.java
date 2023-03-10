package com.kevinlee.webapp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName WebController
 * @Author kevinlee
 * @Date 2022/3/25 15:32
 * @Version 1.0
 **/
@Controller
public class WebController {
    @RequestMapping("/test")
    @ResponseBody
    public String test(@RequestBody JSONArray array){
        List list = JSONObject.parseArray(array.toJSONString());
        return JSON.toJSONString(list);
    }
}
