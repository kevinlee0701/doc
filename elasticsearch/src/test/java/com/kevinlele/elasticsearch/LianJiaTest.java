package com.kevinlele.elasticsearch;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.kevinlee.elasticsearch.Court;
import com.kevinlee.elasticsearch.ElasticsearchApplication;
import com.kevinlee.elasticsearch.ResultData;
import com.kevinlee.elasticsearch.config.JsoupWhitelistUntil;
import com.kevinlee.elasticsearch.mybatis.Lhy;
import com.kevinlee.elasticsearch.mybatis.LianJiaMapper;
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
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.ParsedAvg;
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
import org.springframework.data.elasticsearch.core.AggregationsContainer;
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
import java.util.stream.Collectors;

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
    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Resource
    private LianJiaMapper lianJiaMapper;
    @Test
    public void lianJiaMapper(){

    }
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
        //List<Court> bjCourts = Arrays.asList(Court.LONGHUAYUAN,Court.XINLONGCHENG,Court.LONGTENGYUAN);
        List<Court> bjCourts = Arrays.asList(Court.LONGHUAYUAN);
        String city = "bj";
        for (Court bjCourt : bjCourts) {
            boolean flag = lianjia(bjCourt.getCourt(), city);
            saveMysql(bjCourt, flag);
        }
    }

    private void saveMysql(Court bjCourt, boolean flag) throws InterruptedException {
        log.info("开始数据库操作：flag={}", flag);
        Thread.sleep(1000);
        if (flag) {
            if(bjCourt == Court.LONGHUAYUAN){//龙华园多查一次70-80平米的数据
                Map<String, Double> map70 = this.queryAvg(Court.LONGHUAYUAN70.getCourt(), Court.LONGHUAYUAN70.getGteArea(), Court.LONGHUAYUAN70.getLteArea(), Court.LONGHUAYUAN70.getHourseInfo());
                log.info("搜索引擎查询结果：map={}", map70);
                if (!map70.isEmpty()) {
                    Double avgeTotalPrice = Math.round(map70.get("avgeTotalPrice") * 100.0) / 100.0;
                    Double avgePrice = Math.round(map70.get("avgePrice") * 100.0) / 100.0;
                    Lhy lhy = new Lhy();
                    lhy.setTotalPrice(avgeTotalPrice);
                    lhy.setSouthTotalPrice(avgeTotalPrice);
                    lhy.setCreateTime(new Date());
                    lhy.setPrice(avgePrice);
                    lhy.setSouthPrice(avgePrice);
                    lhy.setCourt(Court.LONGHUAYUAN70.getAddress());
                    lianJiaMapper.insert(lhy);
                }
            }
            if(bjCourt == Court.JINKECHENG){//金科城多查一次80平米的房子
                Map<String, Double> map70 = this.queryAvg(Court.JINKECHENG80.getCourt(), Court.JINKECHENG80.getGteArea(), Court.JINKECHENG80.getLteArea(), Court.JINKECHENG80.getHourseInfo());
                log.info("搜索引擎查询结果：map={}", map70);
                if (!map70.isEmpty()) {
                    Double avgeTotalPrice = Math.round(map70.get("avgeTotalPrice") * 100.0) / 100.0;
                    Double avgePrice = Math.round(map70.get("avgePrice") * 100.0) / 100.0;
                    Lhy lhy = new Lhy();
                    lhy.setTotalPrice(avgeTotalPrice);
                    lhy.setSouthTotalPrice(avgeTotalPrice);
                    lhy.setCreateTime(new Date());
                    lhy.setPrice(avgePrice);
                    lhy.setSouthPrice(avgePrice);
                    lhy.setCourt(Court.JINKECHENG80.getAddress());
                    lianJiaMapper.insert(lhy);
                }
            }
            Map<String, Double> map = this.queryAvg(bjCourt.getCourt(), bjCourt.getGteArea(), bjCourt.getLteArea(), bjCourt.getHourseInfo());
            log.info("搜索引擎查询结果：map={}", map);
            if (!map.isEmpty()) {
                Double avgeTotalPrice = Math.round(map.get("avgeTotalPrice") * 100.0) / 100.0;
                Double avgePrice = Math.round(map.get("avgePrice") * 100.0) / 100.0;
                Lhy lhy = new Lhy();
                lhy.setTotalPrice(avgeTotalPrice);
                lhy.setSouthTotalPrice(avgeTotalPrice);
                lhy.setCreateTime(new Date());
                lhy.setPrice(avgePrice);
                lhy.setSouthPrice(avgePrice);
                lhy.setCourt(bjCourt.getAddress());
                lianJiaMapper.insert(lhy);
            }
        }
    }


    /**
     * 洛阳房价
     * @throws Exception
     */
    @Test
    public void luoyang() throws Exception {
        String xiaoqu ="鈺泰九龍苑";
        String[] luoyangCourts=new String[]{xiaoqu};
        String city="luoyang";
        for (String court : luoyangCourts) {
            boolean flag = lianjia(court, city);
            log.info("开始数据库操作：flag={}",flag);
            if(flag){
                Map<String, Double> map = queryAvg(court, 100, 103, "南 北");
                if (!map.isEmpty()){
                    Double avgeTotalPrice = map.get("avgeTotalPrice");
                    Double avgePrice = map.get("avgePrice");
                    Lhy lhy = new Lhy();
                    lhy.setPrice(avgeTotalPrice);
                    lhy.setSouthPrice(avgeTotalPrice);
                    lhy.setCreateTime(new Date());
                    lhy.setPrice(avgePrice);
                    lhy.setSouthPrice(avgePrice);
                    lhy.setCourt("鈺泰九龍苑(100-103)");
                    lianJiaMapper.insert(lhy);
                }
            }
        }
    }
    public Map queryAvg(String address,int gte,int lte,String houseInfo){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.prefixQuery("address.keyword",address));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("area").gte(gte).lte(lte));
        boolQueryBuilder.must(QueryBuilders.matchQuery("houseInfo",houseInfo));
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withAggregations(AggregationBuilders.avg("avgeTotalPrice").field("totalPrice"))
                .withAggregations(AggregationBuilders.avg("avgePrice").field("unitPrice"));
        NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();
        SearchHits<LianJia> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, LianJia.class);
        Aggregations aggregations =(Aggregations) searchHits.getAggregations().aggregations();
        Map<String, Aggregation> asMap = aggregations.getAsMap();
        Map result = new HashMap<String,Double>();
        result.put("avgeTotalPrice",((ParsedAvg)asMap.get("avgeTotalPrice")).getValue());
        result.put("avgePrice",((ParsedAvg)asMap.get("avgePrice")).getValue());
        return result;
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
        String[] luoyangCourts=new String[]{"金科城"};
        String city="zz";
        for (String court : luoyangCourts) {
            lianjia(court,city);
        }
    }

    /**
     * 查询链家小区报价
     * @param court 小区名称
     * @param city 城市代码
     * @throws IOException
     */
    private  boolean lianjia(String court,String city) throws IOException {
        boolean flag =false;
        ArrayList<LianJia> lianJias = new ArrayList<>();
        Date createDate = new Date();
        for (int i = 1; i < 6; i++) {
            log.info(" ======= 城市：{},小区：{}，第 {} 页================",city,court,i);
            String url = "https://"+city+".lianjia.com/ershoufang/pg" + i + "rs"+court+"/";
            if (i == 1) {
                url = "https://"+city+".lianjia.com/ershoufang/rs"+court+"/";
            }
            boolean xinxi = xinxi(lianJias, url, createDate,city,court);
            if(xinxi){
                break;
            }
        }
        if (!lianJias.isEmpty()){
            flag= true;
            List<LianJia> jiaByAddress = lianJiaDao.findLianJiaByAddress(court);
            lianJiaDao.deleteAll(jiaByAddress);
            lianJiaDao.saveAll(lianJias);
        }
        lianJias.clear();
        return flag;
    }
@Test
public void deleteAll() throws IOException {
    List<LianJia> list = lianJiaDao.findLianJiaByAddress("龙华苑");
    System.out.println(list);
    if(!list.isEmpty())
        lianJiaDao.deleteAll(list);
}

    private  boolean xinxi(ArrayList<LianJia> lianJias, String url, Date createDate,String city,String court) throws IOException {
        Document document =null;
        if(city.equals("bj")){
            Map<String, String> cookies = new HashMap<>();
            cookies.put("hip", "D-6cMuFNWUoBW3EVx54hyKi69GM8YR6UY_ap2UZ1vajuR7kKNvGO5UkvQeBWwjnxgCCebpwALhiwyhVigI6sGyyRJy-iwE84EVjndrn5e3R5Y09pQuUdOTQuKLVz1OlGls-THGAOumBLwVZ6LWLNdgKPWtRtk9ExV3vV7V5zEmQIsq52NsHiwh-inw%3D%3D");
            cookies.put("Hm_lpvt_46bf127ac9b856df503ec2dbf942b67e", "1728442820");
            cookies.put("Hm_lvt_46bf127ac9b856df503ec2dbf942b67e", "1726220298,1727244521,1727678045");

            cookies.put("HMACCOUNT", "D21A575A29D5136A");
            cookies.put("lianjia_ssid","98220bd2-a3e2-4b76-8b01-002c0092d150");
            cookies.put("lianjia_uuid","100dc6aa-f9fe-4bee-9972-146ecd0662cf");
            cookies.put("select_city","110000");
            cookies.put("srcid","eyJ0Ijoie1wiZGF0YVwiOlwiNjI3NjVhZWFkZTkwNjk2MDAzOGNjMDU0NDE2Mjc1MzMyMjYzMjkzNWZlYjExMWJhYWE3YjYzMmQyNzNjNTJjNWNjNmFiMzAwNWIwZmQ0ZTViODYyM2FkNGQwZjc0NzNiN2Y1OTczMjQyOGQ2Y2EyMGNkN2ExNGJjNmQ1ZGMxMWQ5Nzc1YTkxMWIxMDJmNDMwN2Q0MjlhZjI4MWVhYWU1ZTMwZGZkMjdkYWZmMjcyZmYyNmU5NzFlNjFkZjVmMmNhOWZlMGNlYmFlMzlkYjI3MmJmMjBjZWExMjlhODdlOGY0MWI3MzUzNDg5Y2FkMjA3ZmE2NzMyOThlMzkyNDg3MVwiLFwia2V5X2lkXCI6XCIxXCIsXCJzaWduXCI6XCI5N2UzMzk2YVwifSIsInIiOiJodHRwczovL2JqLmxpYW5qaWEuY29tL2Vyc2hvdWZhbmcvMTAxMTI1NzE4MzM0Lmh0bWwiLCJvcyI6IndlYiIsInYiOiIwLjEifQ==");
            document = Jsoup.connect(url).cookies(cookies).timeout(30000).get();
        }else{
            Map<String, String> cookies = new HashMap<>();
            cookies.put("beikeBaseData", "%7B%22parentSceneId%22%3A%22%22%7D");
            cookies.put("crosSdkDT2019DeviceId", "x6sic0--y3e1di-3p2fnhbbl8wjiu3-zwl80wwuc");
            cookies.put("ftkrc_", "6ce66a6c-9c3a-4728-9272-a7b85629e296");
            cookies.put("Hm_lpvt_46bf127ac9b856df503ec2dbf942b67e", "1726282585");
            cookies.put("HMACCOUNT", "FE28DC61974B5298");
            cookies.put("Hm_lvt_46bf127ac9b856df503ec2dbf942b67e","1725533824,1726220298");
            cookies.put("lfrc_","385590b8-9612-4be1-851c-ca1f07d3367f");
            cookies.put("lianjia_ssid","f2e17c20-36de-4704-962c-5c431b6e2674");

            cookies.put("lianjia_token","2.0010abc3dd6a5622710106eaec9b46a622");
            cookies.put("lianjia_token_secure","2.0010abc3dd6a5622710106eaec9b46a622");
            cookies.put("lianjia_uuid","dff2e6e4-132f-4c5a-bd0b-8b809e4f1025");
            cookies.put("login_ucid","2000000006196288");
            cookies.put("security_ticket","kiHKZ2/3uSfFI5GRfllb/LO/4NlI6gVrB6N+6aJwrD9A9mOcEkQtRegiu26sOdf5BsiWAgUaMbyVGIswaCxgokANurJB0Ramh71l0jH0FLX7t7l8Jp8gT/TFlxUJG2moKKfSIU1ssiEB3pQ/0iq/GNyIm8lVkypvod7cDaz4Jcg=");
            if(city.equals("luoyang")){
                cookies.put("select_city","410300");
            }else if(city.equals("zz")){
                cookies.put("select_city","410100");
            }
            cookies.put("sensorsdata2015jssdkcross","%7B%22distinct_id%22%3A%22191eaebd23f1add-09c17531646aae-17525637-4953600-191eaebd24014e5%22%2C%22%24device_id%22%3A%22191eaebd23f1add-09c17531646aae-17525637-4953600-191eaebd24014e5%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E8%87%AA%E7%84%B6%E6%90%9C%E7%B4%A2%E6%B5%81%E9%87%8F%22%2C%22%24latest_referrer%22%3A%22https%3A%2F%2Fwww.google.com%2F%22%2C%22%24latest_referrer_host%22%3A%22www.google.com%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC%22%7D%7D");
            cookies.put("srcid","eyJ0Ijoie1wiZGF0YVwiOlwiZTQ1ODlmYzNkMDM1MTE4OTA1ZmFhMGZmYzMyYjU2OTEyNDYyZTFmOTI2Njg2MzdmZTliMDUxMmVjZmEwZTJlY2EyOTEwMjQxYTVlYjdkNzAyZTVhYWVjNzEyNWFjMWQ4ZGYxNDQzZWU0YmI3YzJiOWNhYjk1NDUyMmM3ZTE1MzkxMGNlNDA4YjQ4ZGQyOGY5MjIxZjdiYzM3YmVkYjA5ZDBlNTgxODJmMWUzOTlhMjA5YmQzNmQ5M2U4NGFjNDE2YjMzYjIyZWEyM2U1ODhlZDA3YzQwNTc5MGRkZTNhMDkzZmJmNzI2NDU0MjE5Yjk1NWY2OTg5MmFhOWZhNjIwN1wiLFwia2V5X2lkXCI6XCIxXCIsXCJzaWduXCI6XCJlZjc5ODBlOVwifSIsInIiOiJodHRwczovL2x1b3lhbmcubGlhbmppYS5jb20vZXJzaG91ZmFuZy9ycyVFOSU5MiVCMCVFNiVCMyVCMC8iLCJvcyI6IndlYiIsInYiOiIwLjEifQ==");
            document = Jsoup.connect(url).cookies(cookies).timeout(30000).get();
        }

        if (document == null) {
            log.error("document没有查询到信息，退出操作。");
            return true;
        }

        Elements sellListContent = document.getElementsByClass("sellListContent");
        if (sellListContent == null || sellListContent.isEmpty()) {
            log.error("sellListContent没有查询到信息，退出操作。");
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
                    log.info("url={},houseInfo={},address={},totalPrice={}，unitPrice={},fang_url={}",url,houseInfo, address, totalPrice, unitPrice,fang_url);
                    if (!address.contains(court)) {
                        log.warn("地址信息与输入地址信息不匹配，court ={},adress={}", court, address);
                        return true;
                    }
                    LianJia lianJia = new LianJia();
                    lianJia.setId(address + "-" + totalPrice + "-" + unitPrice + "-" + html);
                    if(fang !=null) {
                        String remake = fang.get("taonei") + " ## " + fang.get("guapai") + " ## " + fang.get("shangci") + " ## " + fang.get("nianxian") + " ## " + fang.get("louceng") + " ## " + fang.get("mianji");
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
        try {
            Random random = new Random();
            int num = random.nextInt(9)+1;
            log.info("====== 睡眠时间【{}】秒",num);
            Thread.sleep(num*1000);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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



    @Test
    public void testLianjia2(){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.prefixQuery("address.keyword","龙华园"));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("area").gte(80).lte(83));
        boolQueryBuilder.must(QueryBuilders.matchQuery("houseInfo","南 北"));
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withAggregations(AggregationBuilders.avg("avgeTotalPrice").field("totalPrice"))
                .withAggregations(AggregationBuilders.avg("avgePrice").field("unitPrice"));
        NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();
        SearchHits<LianJia> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, LianJia.class);
        Aggregations aggregations =(Aggregations) searchHits.getAggregations().aggregations();
        aggregations.getAsMap().forEach((key,value)->{
            System.out.println(key+"===="+((ParsedAvg) value).getValue());
        });

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
