package com.kevinlee.utis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 类 描 述：测试RestTemplate
 * 创建时间：2023/9/21 17:26
 * 创 建 人：lifeng
 */
@Slf4j
public class HttpUtils {
    @Test
    public void test1(){
       String url="https://daxue-cos..com/shark/attachment/2144d34096be480bbd1f427811a24301/音频(纯单词版)Unit 01.mp3";
       url = url.replace(" ", "%20");
       System.out.println(url);
       URI uri = URI.create(url);
    }




    private static String getEnCodeUrl(String url){
        try {
            URI uri = URI.create(url);
            return uri.toASCIIString();
        } catch (Exception e) {
            log.error("编码异常：url={}", url, e);
        }
        return url;
    }
    @Test
    public void testHttp(){
        String domain="http://basa.earn.com";
       String application="jiaofu-dubbo-ttime";
       int pageSize =5;
       int page=1;
       String url= domain+"/api/dev/service?pattern=application&filter="+application+"&page="+0+"&size="+pageSize;
        String s = HttpUtil.get(url, null);
        Set<String> result = new HashSet<>();
        if(!s.isEmpty()){
            JSONObject jsonObject = JSON.parseObject(s);
            Object totalElements = jsonObject.get("totalElements");
            JSONArray content = (JSONArray) jsonObject.get("content");
            int total = (int) totalElements;
            int totalpage = (total + pageSize - 1) / pageSize;
            for (Object o : content) {
                JSONObject json = (JSONObject) o;
                String service= json.get("service").toString();
                result.add(service);
            }
            while (page<totalpage){
                log.info("==========={}",page);
                page++;
                url= domain+"/api/dev/service?pattern=application&filter="+application+"&page="+(page-1)+"&size="+pageSize;
                String s1 = HttpUtil.get(url, null);
                if(!s1.isEmpty()){
                    JSONObject jsonObject1 = JSON.parseObject(s1);
                    JSONArray content1= (JSONArray) jsonObject1.get("content");
                    for (Object o : content1) {
                        JSONObject json = (JSONObject) o;
                        String service= json.get("service").toString();
                        result.add(service);
                    }
                }
            }
        }
        log.info("result+++++++++++++++++++"+result);
        HashSet<String> consumerList = new HashSet<>();
        Map<String,HashSet<String>>  serviceToAppliaction = new HashMap<>();
        for (String service : result) {
            String fuwu = HttpUtil.get(domain+"/api/dev/service/" + service, null);
            if(!fuwu.isEmpty()){
                HashSet<String> serviceToApplicationMap = new HashSet<>();
                serviceToAppliaction.put(service,serviceToApplicationMap);
                JSONObject jsonObject = JSON.parseObject(fuwu);
                Object consumers = jsonObject.get("consumers");
                if (consumers!=null){
                    JSONArray List = (JSONArray) consumers;
                    for (Object o : List) {
                        JSONObject json = (JSONObject) o;
                        consumerList.add(json.get("application").toString());
                        serviceToApplicationMap.add(json.get("application").toString());
                    }
                }
            }
        }
        log.info("=========="+application+"==========");
        for (String s1 : consumerList) {
            log.info(s1);
        }
        log.info("========== end ==========");
        log.info("========== 方法对应的应用 =========");
        if(!serviceToAppliaction.isEmpty()){
            for (String key : serviceToAppliaction.keySet()) {
                HashSet<String> value = serviceToAppliaction.get(key);
                if(!value.isEmpty())
                    log.info(key+"="+value);
            }
        }
        log.info("========== end ==========");

    }
}
