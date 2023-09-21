package com.kevinlee.utis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

/**
 * 类 描 述：测试RestTemplate
 * 创建时间：2023/9/21 17:26
 * 创 建 人：lifeng
 */
public class HttpUtils {

    @Test
    public void testHttp(){
       String application="jiaofu-dubbo-pigai";
       int pageSize =10;
       int page=0;
       String url= "http://basa.koolearn.com/api/dev/service?pattern=application&filter="+application+"&page="+page+"&size="+pageSize;

        String s = HttpUtil.get(url, null);
        if(!s.isEmpty()){
            JSONObject jsonObject = JSON.parseObject(s);
            Object totalElements = jsonObject.get("totalElements");
            JSONArray content = (JSONArray) jsonObject.get("content");
            int total = (int) totalElements;
            System.out.println("total=========="+total);
            for (Object o : content) {
                JSONObject json = (JSONObject) o;
                String service= json.get("service").toString();
                System.out.println("service==========================="+service);
            }
        }


    }
}
