package com.kevinlele.elasticsearch;

import com.kevinlee.elasticsearch.ElasticsearchApplication;
import com.kevinlee.elasticsearch.pachong.LianJia;
import com.kevinlee.elasticsearch.pachong.LianJiaDao;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ArrayList;

/**
 * 类 描 述： 龙华园在卖数据
 * 创建时间：2024/1/2 17:17
 * 创 建 人：lifeng
 */
@Slf4j
@SpringBootTest(classes = ElasticsearchApplication.class)
@RunWith(SpringRunner.class)
public class LianJiaTest {
    @Resource
    private LianJiaDao lianJiaDao;
    @Test
    public void test(){
       String s= "2室1厅 | 81.24平米 | 西 东 | 精装 | 6层 | 板楼";
        String[] split = s.split("\\|");
        System.out.println(split[1].replace("平米",""));
    }

    @Test
    public void testLianjia() throws Exception {
        ArrayList<LianJia> lianJias = new ArrayList<>();
        for(int i=1;i<1000;i++){
            String  url = "https://bj.lianjia.com/ershoufang/pg"+i+ "rs%E9%BE%99%E5%8D%8E%E5%9B%AD/";
            if(i==1){
                url="https://bj.lianjia.com/ershoufang/rs%E9%BE%99%E5%8D%8E%E5%9B%AD/";
            }

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
                        String houseInfo = el.getElementsByClass("houseInfo").eq(0).text();
                        String address = el.getElementsByClass("positionInfo").eq(0).text();
                        String totalPrice = el.getElementsByClass("totalPrice").eq(0).text().replace("万","");
                        String unitPrice = el.getElementsByClass("unitPrice").attr("data-price");
                        String img = el.getElementsByClass("lj-lazy").attr("src");
                        String html = el.getElementsByClass("title").get(0).getElementsByTag("a").attr("href");
                        String area = houseInfo.split("\\|")[1].replace("平米","");
                        log.info("url={},address={},totalPrice={}，unitPrice={},img={}",url,address,totalPrice,unitPrice,img);
                        LianJia lianJia = new LianJia();
                        lianJia.setId(address+"-"+totalPrice+"-"+unitPrice+"-"+html);
                        lianJia.setRemark("昌平-两室-200-200:250");
                        lianJia.setHouseInfo(houseInfo);
                        lianJia.setArea(Double.parseDouble(area));
                        lianJia.setAddress(address);
                        lianJia.setTotalPrice(Double.parseDouble(totalPrice));
                        lianJia.setUnitPrice(Double.parseDouble(unitPrice));
                        lianJia.setImg(img);
                        lianJia.setHtml(html);
                        lianJias.add(lianJia);
                    }
                }
            }
        }
        log.info("list={}",lianJias.size());
        lianJiaDao.deleteAll();
        lianJiaDao.saveAll(lianJias);

    }
}
