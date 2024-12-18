package com.kevinlele.elasticsearch;


import com.kevinlee.elasticsearch.Court;
import com.kevinlee.elasticsearch.ElasticsearchApplication;
import com.kevinlee.elasticsearch.ResultData;
import com.kevinlee.elasticsearch.mybatis.Lhy;
import com.kevinlee.elasticsearch.mybatis.LianJiaMapper;
import com.kevinlee.elasticsearch.pachong.LianJia;
import com.kevinlee.elasticsearch.pachong.LianJiaDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.ParsedAvg;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
        Map<String, Double> map = queryAvg(Court.LONG_TENG_YUAN.getCourt(), Court.LONG_TENG_YUAN.getGteArea(), Court.LONG_TENG_YUAN.getLteArea(), "南 北");
        System.out.println(Math.round(map.get("avgeTotalPrice") * 100.0) / 100.0);
    }
    @Test
    public void test1(){
        System.out.println(lianJiaDao.findById("ceshi"));

    }
    @Test
    public void test12(){
      LianJia lianJia = new LianJia();
      lianJia.setId("ceshi2");
      lianJia.setCreateTime(new Date());
      lianJiaDao.save(lianJia);
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
        List<Court> bjCourts = Arrays.asList(Court.LONG_TENG_YUAN,Court.YUN_QU_YUAN,Court.LONG_HUA_YUAN);
//        List<Court> bjCourts = Arrays.asList(Court.XIN_LONG_CHENG,Court.LONG_HUA_YUAN,Court.LONG_TENG_YUAN);
        String city = "bj";
        for (Court bjCourt : bjCourts) {
            boolean flag = lianjia(bjCourt.getCourt(), city);
            saveMysqlNew(bjCourt, flag);
        }
    }

    @Test
    public void testMysql() throws InterruptedException {
        this.saveMysqlNew(Court.JIN_KE_CHENG,true);
    }
    private void saveMysqlNew(Court bjCourt, boolean flag) throws InterruptedException {
        log.info("开始数据库操作：flag={}", flag);
        Thread.sleep(1000);
        if (flag) {
            for (Court yuan : bjCourt.values()) {
                if(yuan.getCourt().equals(bjCourt.getCourt())){
                    Map<String, Double> map = this.queryAvg(yuan.getCourt(), yuan.getGteArea(), yuan.getLteArea(), yuan.getHourseInfo());
                    log.info("搜索引擎查询结果：yuan={}，map={}",yuan,map);
                    if (!map.isEmpty()) {
                        Double avgeTotalPrice = map.get("avgeTotalPrice");
                        Double avgePrice =  map.get("avgePrice");
                        Lhy lhy = new Lhy();
                        lhy.setTotalPrice(avgeTotalPrice);
                        lhy.setSouthTotalPrice(avgeTotalPrice);
                        lhy.setCreateTime(new Date());
                        lhy.setPrice(avgePrice);
                        lhy.setSouthPrice(avgePrice);
                        lhy.setCourt(yuan.getCourt());
                        lhy.setRemark(yuan.getAddress());
                        lhy.setTotalHits(map.get("totalHits"));
                        lianJiaMapper.insert(lhy);
                    }
                }

            }
        }
    }


    /**
     * 洛阳房价
     * @throws Exception
     */
    @Test
    public void luoyang() throws Exception {
        List<Court> bjCourts = Arrays.asList(Court.YU_TAI_JIU_LONG_YUAN);
        String city = "luoyang";
        for (Court bjCourt : bjCourts) {
            boolean flag = lianjia(bjCourt.getCourt(), city);
            saveMysqlNew(bjCourt, flag);
        }
    }
    public Map queryAvg(String address,int gte,int lte,String houseInfo){
        log.info("queryAvg == 参数：address={},gte={},lte={},houseInfo={}", address,gte,lte,houseInfo);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.prefixQuery("address.keyword",address));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("area").gte(gte).lte(lte));
        if(!houseInfo.isEmpty()){
            boolQueryBuilder.must(QueryBuilders.matchQuery("houseInfo",houseInfo));
        }
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withAggregations(AggregationBuilders.avg("avgeTotalPrice").field("totalPrice"))
                .withAggregations(AggregationBuilders.avg("avgePrice").field("unitPrice"));
        NativeSearchQuery nativeSearchQuery = nativeSearchQueryBuilder.build();
        SearchHits<LianJia> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, LianJia.class);
        long totalHits = searchHits.getTotalHits();
        double avgeTotalPrice=0;
        double avgePrice=0;
        Map result = new HashMap<String,Double>();
        if(totalHits>0){
            Aggregations aggregations =(Aggregations) searchHits.getAggregations().aggregations();
            Map<String, Aggregation> asMap = aggregations.getAsMap();
            avgeTotalPrice = Math.round(((ParsedAvg) asMap.get("avgeTotalPrice")).getValue() * 100.0) / 100.0;
            avgePrice = Math.round(((ParsedAvg) asMap.get("avgePrice")).getValue() * 100.0) / 100.0;

        }
        result.put("avgeTotalPrice",avgeTotalPrice);
        result.put("avgePrice",avgePrice);
        result.put("totalHits",Double.parseDouble(totalHits+""));
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
        List<Court> bjCourts = Arrays.asList(Court.JIN_KE_CHENG);
        String city = "zz";
        for (Court bjCourt : bjCourts) {
            boolean flag = lianjia(bjCourt.getCourt(), city);
            saveMysqlNew(bjCourt, flag);
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
    List<LianJia> list = lianJiaDao.findLianJiaByAddress("云趣园");
    System.out.println(list);
    if(!list.isEmpty())
        lianJiaDao.deleteAll(list);
}

    private  boolean xinxi(ArrayList<LianJia> lianJias, String url, Date createDate,String city,String court) throws IOException {
        Document document =null;
        Map<String, String> cookies = new HashMap<>();
        if(city.equals("bj")){
            cookies.put("crosSdkDT2019DeviceId", "-bfft8o--oqcgyn-ooymqheu0ridbdk-jan5ftdej");
            cookies.put("digv_extends", "%7B%22utmTrackId%22%3A%22%22%7D");
            cookies.put("Hm_lvt_46bf127ac9b856df503ec2dbf942b67e", "1727244521,1727678045,1728896803,1728899119");

            cookies.put("HMACCOUNT", "164EB95D10285F27");
            cookies.put("ftkrc_","134883fa-fd15-4d9c-a495-d91d589d51cb");
            cookies.put("Hm_lpvt_46bf127ac9b856df503ec2dbf942b67e", "1730715809");
            cookies.put("Hm_lvt_46bf127ac9b856df503ec2dbf942b67e","1728896803,1728899119,1729587242");
            loginLianjia(cookies);

            cookies.put("select_city","110000");
            cookies.put("srcid","eyJ0Ijoie1wiZGF0YVwiOlwiNDkyMzhjMjRjMGEwNmRmNzZjMzRhNDUxYmFmNTFjN2E1MzBiODdkZDVkNDgyYzc0ZGE5ODk1NmU5Yzc2ZDQ5YmUxZjliZmI5MmI4N2Y3N2Y2MDZkZWEyNGVkNzUyYzUyMzE1ZjcwMGNiMTBiYzkwOWE4NjU2ZDVjNzRmOTQ1Yzk4ZjI5ZGM1ZDcyOGI3NjRhZTQyYjdmZjAxMTU2YmRiZGY0ZDQwMjNhYjJiMzAyODNiNjFiYWIwYWM4NDk2ZDMyNTI4MjIyMjQyOGU1MTU2OTFiYjQ3MWYxNjNiODVkNDY1NWUyNzc1ZDYyOWUzNDFiZGRhMDk0NmJkNjUyY2YxZlwiLFwia2V5X2lkXCI6XCIxXCIsXCJzaWduXCI6XCI4YTVhMzBiMFwifSIsInIiOiJodHRwczovL2JqLmxpYW5qaWEuY29tL2Vyc2hvdWZhbmcvcnMlRTklQkUlOTklRTUlOEQlOEUlRTUlOUIlQUQvIiwib3MiOiJ3ZWIiLCJ2IjoiMC4xIn0=");
            document = Jsoup.connect(url).cookies(cookies).timeout(30000).get();
        }else{
            cookies.put("beikeBaseData", "%7B%22parentSceneId%22%3A%22%22%7D");
            cookies.put("crosSdkDT2019DeviceId", "x6sic0--y3e1di-3p2fnhbbl8wjiu3-zwl80wwuc");
            cookies.put("ftkrc_", "6ce66a6c-9c3a-4728-9272-a7b85629e296");
            cookies.put("Hm_lpvt_46bf127ac9b856df503ec2dbf942b67e", "1726282585");
            cookies.put("HMACCOUNT", "FE28DC61974B5298");
            cookies.put("Hm_lvt_46bf127ac9b856df503ec2dbf942b67e","1725533824,1726220298");
            cookies.put("lfrc_","385590b8-9612-4be1-851c-ca1f07d3367f");
            loginLianjia(cookies);

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
                    Map<String, String> fang = fang(fang_url,cookies);
                    String houseInfo = el.getElementsByClass("houseInfo").eq(0).text();
                    String address = el.getElementsByClass("positionInfo").eq(0).text();
                    String totalPrice = el.getElementsByClass("totalPrice").eq(0).text().replace("万", "");
                    String unitPrice = el.getElementsByClass("unitPrice").attr("data-price");
                    String img = el.getElementsByClass("lj-lazy").attr("src");
                    String html = el.getElementsByClass("title").get(0).getElementsByTag("a").attr("href");
                    String area = houseInfo.split("\\|")[1].replace("平米", "");

                    if (!address.contains(court)) {
                        log.warn("地址信息与输入地址信息不匹配，court ={},address={}", court, address);
                        return true;
                    }
                    LianJia lianJia = new LianJia();
                    lianJia.setId(address + "-" + totalPrice + "-" + unitPrice + "-" + html);
                    if(fang !=null) {
                        String remake = fang.get("taonei") + " ## " + fang.get("guapai") + " ## " + fang.get("shangci") + " ## " + fang.get("nianxian") + " ## " + fang.get("louceng") + " ## " + fang.get("mianji") +" ## "+fang.get("chaoxiang");
                        lianJia.setRemark(remake);
                    }else{
                        lianJia.setRemark(title);
                    }
                    houseInfo = houseInfo+(",楼层："+fang== null?"无信息":fang.get("louceng"));
                    log.info("url={},houseInfo={},address={},totalPrice={}，unitPrice={},楼层：{},朝向：{},fang_url={}",url,houseInfo, address, totalPrice, unitPrice,fang== null?"":fang.get("louceng"),fang== null?"":fang.get("chaoxiang"),fang_url);
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

    private static void loginLianjia(Map<String, String> cookies) {
        //需要测试一下,是否中账户登录
        cookies.put("lianjia_ssid", "0a6c1136-90fd-4097-9d7a-c66607cc6fc3");
        cookies.put("lianjia_token","2.00140f2ed46cec0c8705a207e5d0dceb60");
        cookies.put("security_ticket","mUNJ3aW61rLqlSz01uwB0uaXKqHZy/Ghxb8625wv1WQ/3zBF7up4Hv9hUyoY1uqx8pbxcPizCVsdq04p9ElZEuey3bixAiAxART7FGQgKW/6T8d8iVwTP1T2ONxgTvaZkysQAbD56tPCifnMiNYpn6XUZwTyArriFgos2BX9Sdk=");
        cookies.put("lianjia_token_secure","2.00140f2ed46cec0c8705a207e5d0dceb60");
        cookies.put("lianjia_uuid","391ae60b-70c1-4f89-b1e3-2ee9727121f3");
        cookies.put("login_ucid","2000000040390591");
    }

    public static Map<String, String> fang(String url,Map<String, String> cookies)  {
        try {
            Random random = new Random();
            int num =8 + random.nextInt(20 - 8 + 1);
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
            document = Jsoup.connect(url).cookies(cookies).timeout(30000).get();
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
                if(li.size()>=7){
                    String chaoxiang= li.get(6).text();
                    result.put("chaoxiang",chaoxiang);//房屋朝向
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
