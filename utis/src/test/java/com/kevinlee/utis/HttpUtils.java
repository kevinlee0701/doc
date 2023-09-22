package com.kevinlee.utis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * 类 描 述：测试RestTemplate
 * 创建时间：2023/9/21 17:26
 * 创 建 人：lifeng
 */
public class HttpUtils {

    @Test
    public void testHttp(){
       String application="jiaofu-dubbo-coach";
       int pageSize =5;
       int page=1;
       String url= "http://basa.koolearn.com/api/dev/service?pattern=application&filter="+application+"&page="+0+"&size="+pageSize;
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
                System.out.println("==========="+page);
                page++;
                url= "http://basa.koolearn.com/api/dev/service?pattern=application&filter="+application+"&page="+(page-1)+"&size="+pageSize;
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
        System.out.println("result+++++++++++++++++++"+result);
        HashSet<String> consumerList = new HashSet<>();
        for (String service : result) {
            String fuwu = HttpUtil.get("http://basa.koolearn.com/api/dev/service/" + service, null);
            if(!fuwu.isEmpty()){
                JSONObject jsonObject = JSON.parseObject(fuwu);
                Object consumers = jsonObject.get("consumers");
                if (consumers!=null){
                    JSONArray List = (JSONArray) consumers;
                    for (Object o : List) {
                        JSONObject json = (JSONObject) o;
                        consumerList.add(json.get("application").toString());
                    }
                }
            }
        }
        System.out.println("consumerList+++++++++++++++++++++++++++++++++"+consumerList);

    }
}
