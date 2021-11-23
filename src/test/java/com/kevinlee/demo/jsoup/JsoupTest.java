package com.kevinlee.demo.jsoup;

import com.kevinlee.demo.es.JDProduct;
import com.kevinlee.demo.es.ProductDao;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
    @Autowired
    private ProductDao productDao;



   /**
    * @description: 爬取京东java商品数据
    * @Author kevinlee
    * @Date  2021/11/22
    **/
    @Test
    public void testJDProduct() throws IOException {
        String  url = "https://search.jd.com/Search?keyword=水壶";
        Document document = Jsoup.parse(new URL(url), 30000);
        Element element = document.getElementById("J_goodsList");
        Elements els = element.getElementsByTag("li");
        List<JDProduct> list = new ArrayList<>();
        for (Element el : els) {
            
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text().replace("￥","").split(" ")[0];
            String title = el.getElementsByClass("p-name").eq(0).text();
            log.info("img={},price={},title={}",img,price,title);
            JDProduct jdProduct = new JDProduct();
            jdProduct.setTitle(title);
            jdProduct.setPrice(price == null? 0:Double.parseDouble(price));
            jdProduct.setImg(img);
            list.add(jdProduct);
        }
        log.info("list={}",list);
//        productDao.saveAll(list);

    }
}
