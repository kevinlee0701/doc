package com.kevinlele.elasticsearch;


import com.alibaba.fastjson.JSONObject;
import com.kevinlee.elasticsearch.ElasticsearchApplication;
import com.kevinlee.elasticsearch.ResultData;
import com.kevinlee.elasticsearch.config.JsoupWhitelistUntil;
import com.kevinlee.elasticsearch.pachong.LianJia;
import com.kevinlee.elasticsearch.pachong.LianJiaDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.server.ExportException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    String tt = "";

    @Test
    public void test1(){
        List<LianJia> jiaByAddress = lianJiaDao.findLianJiaByAddress("ceshiyixia");
        for (LianJia byAddress : jiaByAddress) {
            System.out.println(byAddress);
        }
    }
    @Test
    public void test11(){
        String baseContent = "<p></p>\n" +
                "<div id=\"ai-assist-root-text\" data-v-app=\"\">\n" +
                "<p><span>The construction  of the Erie Canal, which is a famous waterway, faced and overcame three challenges. </span></p>\r\n" +
                "<p><span>The first challenge was that the canal had to be built through forest, however, useful devices had been invited and animals helped to pull the devices which reduce the difficulty of digging in the forest; Additionally, considering that the canal had to pass through wetlands where the workers could easily get malaria, the work were done in winter so the mosquitoes that cause the disease were not active.</span></p>\n" +
                "<p><span>The second challenge was a lack of workers. Nevertheless, the European immigration brought sufficient number of workers who had urgent needs for jobs.</span></p>\n" +
                "<p><span>Finally, the facility of the canal cut down the time and money cost for travelling to the Midwest to a large extent, thus a lot of people moved there and generated enough commerce that covered the cost of construction.</span></p>\n" +
                "</div>";
        baseContent = Jsoup.clean(baseContent, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
        // baseContent = baseContent.replaceAll("\\s{2,}", "\n");
        System.out.println(baseContent);
    }


    @Test
    public void beijing() throws Exception {
 //       String[] bjCourts=new String[]{"龙华园","龙腾苑","新龙城"};
       String[] bjCourts=new String[]{"龙华园"};
        String city="bj";
        for (String court : bjCourts) {
            lianjia(court,city);
        }
    }

    /**
     * 洛阳房价
     * @throws Exception
     */
    @Test
    public void luoyang() throws Exception {
        String[] luoyangCourts=new String[]{"钰泰九龙苑"};
        String city="luoyang";
        for (String court : luoyangCourts) {
            lianjia(court,city);
        }
    }
    @Test
    public void jtest() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = sdf.parse("2024-04-10 15:00:00");
        Date now = new Date();
        long diff = parse.getTime() - now.getTime();
        long min1 = diff /60/1000;
        double min = (double)min1;
        //long min = diff / (1000 * 60);
        System.out.println(Math.abs(min));

        System.out.println(min/60);
    }

    /**
     * 西安房价
     * @throws Exception
     */
    @Test
    public void xian() throws Exception {
        String[] luoyangCourts=new String[]{"群星汇"};
        String city="xa";
        for (String court : luoyangCourts) {
            lianjia(court,city);
        }
    }


    /**
     * 郑州房价
     * @throws Exception
     */
    @Test
    public void zhengzhou() throws Exception {
        String[] luoyangCourts=new String[]{""};
        String city="zz";
        for (String court : luoyangCourts) {
            lianjia(court,city);
        }
    }

    /**
     * 查询链家小区报价
     * @param court 小区名称
     * @param court 城市
     * @throws IOException
     */
    private  void lianjia(String court,String city) throws IOException {
        ArrayList<LianJia> lianJias = new ArrayList<>();
        Date createDate = new Date();
        for (int i = 1; i < 1000; i++) {
            String url = "https://"+city+".lianjia.com/ershoufang/pg" + i + "rs"+court+"/";
            if (i == 1) {
                url = "https://"+city+".lianjia.com/ershoufang/rs"+court+"/";
            }
            boolean xinxi = xinxi(lianJias, url, createDate);
            if(xinxi){
                break;
            }
        }
        List<LianJia> jiaByAddress = lianJiaDao.findLianJiaByAddress(court);
        lianJiaDao.deleteAll(jiaByAddress);
        lianJiaDao.saveAll(lianJias);
        lianJias.clear();
    }
@Test
public void deleteAll() throws IOException {
    List<LianJia> list = lianJiaDao.findLianJiaByAddress("龙华苑");
    System.out.println(list);
    if(!list.isEmpty())
        lianJiaDao.deleteAll(list);
}

    private  boolean xinxi(ArrayList<LianJia> lianJias, String url, Date createDate) throws IOException {
        Document document = Jsoup.parse(new URL(url), 30000);
        if (document == null) {
            return true;
        }
        Elements sellListContent = document.getElementsByClass("sellListContent");
        if (sellListContent == null || sellListContent.isEmpty()) {
            return true;
        }

        for (Element element : sellListContent) {
            Elements els = element.getElementsByTag("li");
            if (els != null && els.size() > 0) {
                for (Element el : els) {
                    String title = el.getElementsByClass("title").eq(0).text();
                    String fang_url = el.getElementsByClass("title").select("a").first().attr("abs:href");
                    Map<String, String> fang = fang(fang_url);
                    String houseInfo = el.getElementsByClass("houseInfo").eq(0).text();
                    String address = el.getElementsByClass("positionInfo").eq(0).text();
                    String totalPrice = el.getElementsByClass("totalPrice").eq(0).text().replace("万", "");
                    String unitPrice = el.getElementsByClass("unitPrice").attr("data-price");
                    String img = el.getElementsByClass("lj-lazy").attr("src");
                    String html = el.getElementsByClass("title").get(0).getElementsByTag("a").attr("href");
                    String area = houseInfo.split("\\|")[1].replace("平米", "");
                    log.info("url={},address={},totalPrice={}，unitPrice={},img={}", url, address, totalPrice, unitPrice, img);
                    LianJia lianJia = new LianJia();
                    lianJia.setId(address + "-" + totalPrice + "-" + unitPrice + "-" + html);
                    if(fang!=null){
                        String remake = fang.get("taonei")+" ## "+fang.get("guapai")+" ## "+fang.get("shangci")+" ## "+fang.get("nianxian")+" ## "+fang.get("louceng")+" ## "+fang.get("mianji");
                        lianJia.setRemark(remake);
                    }else{
                        lianJia.setRemark(title);
                    }
                    lianJia.setHouseInfo(houseInfo);
                    lianJia.setArea(Double.parseDouble(area));
                    lianJia.setAddress(address);
                    lianJia.setTotalPrice(Double.parseDouble(totalPrice));
                    lianJia.setUnitPrice(Double.parseDouble(unitPrice));
                    lianJia.setImg(img);
                    lianJia.setHtml(html);
                    lianJia.setCreateTime(createDate);
                    lianJias.add(lianJia);
                }
            }
        }
        return false;
    }
    public static Map<String, String> fang(String url)  {
        Map<String,String> result= new HashMap<>();
        if(StringUtils.isBlank(url)){
            return null;
        }
        Document document = null;
        try {
            document = Jsoup.parse(new URL(url), 30000);
            if (document == null) {
                return null;
            }
            base(document, result);
            transaction(document,result);
        } catch (IOException e) {
           log.error("获取明细数据出错,url={}",url,e);
        }

        return result;
    }

    /**
     * 房屋交易信息
     * @param document
     * @param result
     */
    private static void transaction(Document document, Map<String, String> result) {
        Elements transaction = document.getElementsByClass("transaction").eq(0);
        for (Element element : transaction) {
            Elements content = element.getElementsByClass("content");
            for (Element element1 : content) {
                Elements li = element1.getElementsByTag("li");
                String guapai = li.get(0).text();//挂牌时间
                String shangci= li.get(2).text();//上次交易时间
                String nianxian= li.get(4).text();//年限
                result.put("guapai",guapai);
                result.put("shangci",shangci);
                result.put("nianxian",nianxian);
            }

        }
    }

    /**
     * 房屋基础信息
     * @param document
     * @param result
     */
    private static void base(Document document, Map<String, String> result) {
        Elements transaction = document.getElementsByClass("base").eq(0);
        for (Element element : transaction) {
            Elements content = element.getElementsByClass("content");
            for (Element element1 : content) {
                Elements li = element1.getElementsByTag("li");
                String louceng = li.get(1).text();
                String mianji= li.get(2).text();
                if(li.size()>4){
                    String taonei= li.get(4).text();
                    result.put("taonei",taonei);
                }
                result.put("louceng",louceng);//楼层
                result.put("mianji",mianji);//面积

            }

        }
    }


    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Test
    public void testLianjia2(){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.prefixQuery("address.keyword","龙华苑"));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("totalPrice").gte(500).lte(600));
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder);
        NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();
        SearchHits<LianJia> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, LianJia.class);

        searchHits.forEach(personSearchHit -> {
            System.out.println(personSearchHit.getContent());
        });

    }
    @Test
    public void testLianjia3(){
        List<LianJia> jiaByAddress = lianJiaDao.findLianJiaByAddress("龙华苑");
        lianJiaDao.deleteAll(jiaByAddress);
    }
    @Test
    public void addDays() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = sdf.parse("2024-4-17");
        System.out.println( TimeUtils.addBusinessDays(parse,16));
    }

    @Test
    public void testResultDate() throws ParseException {
        ResultData<String> result = ResultData.of(200, "成功");
        System.out.println(result);

        ResultData<Integer> resultWithData = new ResultData<>(200, "成功", 123);
        System.out.println(resultWithData);
    }
}
