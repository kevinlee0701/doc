package com.kevinlee.demo.jsoup;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @ClassName JsoupTest
 * @Author kevinlee
 * @Date 2021/11/22 15:15
 * @Version 1.0
 **/
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class JsoupTest {



   /**
    * @description:
    * @Author kevinlee
    * @Date  2021/11/22
    **/
    @Test
    public void test() throws IOException {
        String  url = "https://search.jd.com/Search?keyword=java";
        Document document = Jsoup.parse(new URL(url), 30000);
        Element element = document.getElementById("J_goodsList");
//      log.info("html={}",element.html());
        Elements els = element.getElementsByTag("li");
        for (Element el : els) {
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();
            log.info("img={},price={},title={}",img,price,title);
        }

    }
}
