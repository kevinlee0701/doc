package com.kevinlee.demo.jsoup;

import com.kevinlee.demo.es.JDProduct;
import com.kevinlee.demo.es.LianJia;
import com.kevinlee.demo.es.LianJiaDao;
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
import java.net.MalformedURLException;
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
    @Autowired
    private LianJiaDao lianJiaDao;



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
    /**
     * @description: 爬取链家
     * @Author kevinlee
     * @Date  2021/11/23
     **/
    @Test
    public void testLianjia() throws Exception {
        ArrayList<LianJia> lianJias = new ArrayList<>();
        for(int i=1;i<100;i++){
            String  url = "https://bj.lianjia.com/ershoufang/changping/pg"+i+"l2p1p2";
            Document document = Jsoup.parse(new URL(url), 30000);
            if(document==null){
                break;
            }
            Elements sellListContent = document.getElementsByClass("sellListContent");
            if(sellListContent == null || sellListContent.isEmpty()){
                break;
            }
            for (Element element : sellListContent) {
                Elements els = element.getElementsByTag("li");
                if(els !=null && els.size()>0){
                    for (Element el : els) {
                        String address = el.getElementsByClass("positionInfo").eq(0).text();
                        String totalPrice = el.getElementsByClass("totalPrice").eq(0).text().replace("万","");
                        String unitPrice = el.getElementsByClass("unitPrice").attr("data-price");
                        String img = el.getElementsByClass("lj-lazy").attr("src");
                        log.info("url={},address={},totalPrice={}，unitPrice={},img={}",url,address,totalPrice,unitPrice,img);
                        LianJia lianJia = new LianJia();
                        lianJia.setRemark("昌平-两室-200-200:250");
                        lianJia.setAddress(address);
                        lianJia.setTotalPrice(Double.parseDouble(totalPrice));
                        lianJia.setUnitPrice(Double.parseDouble(unitPrice));
                        lianJia.setImg(img);
                        lianJias.add(lianJia);
                    }
                }
            }
        }
        log.info("list={}",lianJias.size());
        lianJiaDao.saveAll(lianJias);
    }
}
