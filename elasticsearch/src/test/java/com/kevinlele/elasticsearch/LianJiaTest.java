package com.kevinlele.elasticsearch;


import com.alibaba.fastjson.JSONObject;
import com.kevinlee.elasticsearch.ElasticsearchApplication;
import com.kevinlee.elasticsearch.config.JsoupWhitelistUntil;
import com.kevinlee.elasticsearch.pachong.LianJia;
import com.kevinlee.elasticsearch.pachong.LianJiaDao;
import com.koolearn.qti.ExtensionManager;
import com.koolearn.qti.SimpleQtiFacade;
import com.koolearn.qti.custom.KooExtensionPackage;
import com.koolearn.qti.delivery.SimpleDeliveryFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
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
    public void readW() throws Exception {
        try{
            KooExtensionPackage kooExtensionPackage = new KooExtensionPackage();
            ExtensionManager extensionManager = new ExtensionManager(kooExtensionPackage);
            com.koolearn.qti.SimpleQtiFacade simpleQtiFacade = new SimpleQtiFacade(extensionManager);
            com.koolearn.qti.delivery.SimpleDeliveryFacade simpleDeliveryFacade = new SimpleDeliveryFacade(simpleQtiFacade);
            //读-文件
            File readFile = new File("/Users/kevinlee/Desktop/手动—题目内容（2003）.xls");
            HSSFWorkbook readWorkbook = new HSSFWorkbook(FileUtils.openInputStream(readFile));

            HSSFSheet readSheet = readWorkbook.getSheetAt(0);
            int lastRowNum = readSheet.getLastRowNum();
            List<String> error=new ArrayList<>();
            for (int i = 1; i <= lastRowNum; i++) {
                HSSFRow readRow = readSheet.getRow(i);
                //获取当前行最后单元格列号
                //int lastCellNum=readRow.getLastCellNum();
                //读第二列数据
                HSSFCell r1 = readRow.getCell(0);
                HSSFCell r2 = readRow.getCell(1);
                //读出来的数据 进行MD5加密
                //根据需要自己处理数据
                String code = r1.getStringCellValue();
                String tiXml = r2.getStringCellValue();
                System.out.println("第"+(i+1)+"行");
                String qti="";
                try{
                    //写入文件
                    String json = simpleDeliveryFacade.unmarshall(tiXml, 123);
                    qti= handlerQti(json);
                }catch (Exception e){
                    qti="解析失败";
                    error.add(code);
                }

                System.out.println(Jsoup.clean(qti,Whitelist.none()));


                //当前文件创建 一列 输出到3列
                HSSFCell cell3 = readRow.createCell(2);

                cell3.setCellValue(qti);

            }
            System.out.println("错误共有"+error.size()+",明细如下："+error);
            FileOutputStream stream = FileUtils.openOutputStream(readFile);
            readWorkbook.write(stream);
            stream.close();
        }catch (Exception e){
            log.error("系统出错：",e);
            throw new Exception(e);
        }


    }


    private String handlerQti(String json) {
        JSONObject jobj = JSONObject.parseObject(json);

        JSONObject result = new JSONObject();
        String stem = jobj.getString("stem");
        Document stemDocument = Jsoup.parse(stem);
        //题干的内容（不包含样式为纯文本 用途： 展示不需要样式的题目内容，SEO优化中TDK的字段）
        String stemInnerText;
        //切割后的stem字段集合
        List<String> stringList = new ArrayList<>();
        //题干
        stemInnerText = Jsoup.parse(stem).text();
        List<String> stems = new ArrayList<>();
        // mp3的地址
        List<String> listenUrl = new ArrayList<>();
        for (String s : stringList) {
            Document parse = Jsoup.parse(s);
            Elements audio = parse.getElementsByTag("audio");
            parse.getElementsByTag("img").remove();
            s = parse.html();
            //处理题干
            String text = Jsoup.clean(s, new JsoupWhitelistUntil("h2", "style")).replace("\n", "").replace("> <", "><");
            text = stemHandler(text);
            if (!"".equals(text)) {
                stems.add(text);
            }
        }
        Elements imgElements = stemDocument.getElementsByTag("img");
        String[] imgUrl = new String[imgElements.size()];
        for (int i = 0; i < imgElements.size(); i++) {
            //imgUrl[i] = tiCdnUrl + imgElements.get(i).getElementsByAttribute("src").attr("src");
            imgUrl[i] = setDomainNameByUrl(imgElements.get(i).getElementsByAttribute("src").attr("src"), "web");
        }
        result.put("imgUrl", imgUrl);
        result.put("stem", stem);
        return result.toString();
    }

    /**
     * 功能描述:去除一个html片段前无意义的多余换行
     * 举例：&nbsp;<br><br><br>The woman expresses her opinion of the u .<strong>Hello，hlakjdfllkajsdf<br><i>adsfadsfadsf</i></strong><br>&nbsp;&nbsp;";
     *
     * @param stem 题干
     * @return 处理过后的题干
     */
    private String stemHandler(String stem) {
        String result = stem.trim();
        try {
            String reg = "&nbsp;|<br(.*?)>| |</(.*?)>";
            Pattern p = Pattern.compile(reg);
            Matcher m = p.matcher(result);
            while (m.find()) {
                if (m.start() == 0) {
                    result = result.substring(m.end());
                    m = p.matcher(result);
                } else {
                    break;
                }
            }

            result = new StringBuffer(result).reverse().toString();
            String reReg = ">(.\\s*)rb<|;psbn&| ";
            Pattern p2 = Pattern.compile(reReg);
            Matcher m2 = p2.matcher(result);
            while (m2.find()) {
                if (m2.start() == 0) {
                    result = result.substring(m2.end());
                    m2 = p2.matcher(result);
                } else {
                    break;
                }
            }
            result = new StringBuffer(result).reverse().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 功能描述: 根据文件路径拼接域名 暂时解决题库新上传后安卓301下载失败问题
     *
     * @param url      url
     * @param platform 出国站还是app
     * @return java.lang.String
     * @author TanFenHui
     * @date 2021/1/11 4:15 下午
     */
    private String setDomainNameByUrl(String url, String platform) {
        if (StringUtils.isEmpty(url)) {
            return url;
        }
        //前缀
        String prefix = "http://ti.koolearn.com";
        //判断是出国站还是app,是oss还是题库
        if (null != platform && platform.equals("web")) {
            if (url.contains("upload/ti/sardine")) {
                prefix = "https://daxue-cos.koocdn.com";
            } else {
                prefix = "https://ti.koocdn.com";
            }
        } else {
            if (url.contains("upload/ti/sardine")) {
                prefix = "https://daxue-cos.koocdn.com";
            } else {
                prefix = "http://ti.koolearn.com";
            }
        }
        return prefix + url;
    }


    @Test
    public void test2() {

        KooExtensionPackage kooExtensionPackage = new KooExtensionPackage();
        ExtensionManager extensionManager = new ExtensionManager(kooExtensionPackage);
        com.koolearn.qti.SimpleQtiFacade simpleQtiFacade = new SimpleQtiFacade(extensionManager);
        com.koolearn.qti.delivery.SimpleDeliveryFacade simpleDeliveryFacade = new SimpleDeliveryFacade(simpleQtiFacade);
        String json = simpleDeliveryFacade.unmarshall(tt, 123);
        System.out.println(json);
    }

    @Test
    public void test() {
        String s = " \"<?xml version=\"\"1.0\"\" encoding=\"\"UTF-8\"\" ?>\n" +
                "<assessmentItem xmlns=\"\"http://www.imsglobal.org/xsd/imsqti_v2p1\"\"\n" +
                "                xmlns:xsi=\"\"http://www.w3.org/2001/XMLSchema-instance\"\"\n" +
                "                xmlns:k=\"\"http://www.koolearn.com/xsd/koo_extensions\"\"\n" +
                "                xsi:schemaLocation=\"\"http://www.imsglobal.org/xsd/imsqti_v2p1 http://www.imsglobal.org/xsd/imsqti_v2p1.xsd\n" +
                "                                    http://www.koolearn.com/xsd/koo_extensions http://www.koolearn.com/xsd/koo_extensions.xsd\"\"\n" +
                "                identifier=\"\"ITEM_2173477\"\" title=\"\"\"\" adaptive=\"\"false\"\" timeDependent=\"\"false\"\">\n" +
                "        <responseDeclaration identifier=\"\"RESPONSE\"\" cardinality=\"\"single\"\" baseType=\"\"string\"\">\n" +
                "            <correctResponse>\n" +
                "                <value>&amp;amp;nbsp;</value>\n" +
                "            </correctResponse>\n" +
                "        </responseDeclaration>\n" +
                "        <outcomeDeclaration identifier=\"\"KEYWORDS\"\" cardinality=\"\"single\"\" baseType=\"\"string\"\">\n" +
                "            <defaultValue>\n" +
                "                <value>[]</value>\n" +
                "            </defaultValue>\n" +
                "        </outcomeDeclaration>\n" +
                "    <outcomeDeclaration identifier=\"\"PASSED\"\" cardinality=\"\"single\"\" baseType=\"\"boolean\"\">\n" +
                "        <defaultValue>\n" +
                "            <value>false</value>\n" +
                "        </defaultValue>\n" +
                "    </outcomeDeclaration>\n" +
                "    <outcomeDeclaration identifier=\"\"SCORE\"\" cardinality=\"\"single\"\" baseType=\"\"float\"\">\n" +
                "        <defaultValue>\n" +
                "            <value>0.0</value>\n" +
                "        </defaultValue>\n" +
                "    </outcomeDeclaration>\n" +
                "    <itemBody>\n" +
                "        <div label=\"\"SUBITEM_0;writing;SUBJECTIVE\"\">\n" +
                "            &amp;lt;p class=&amp;quot;MsoNormal&amp;quot; style=&amp;quot;font-size: 12px; font-family: Simsun; line-height: 13.5pt;&amp;quot;&amp;gt;&amp;lt;span style=&amp;quot;font-size: larger;&amp;quot;&amp;gt;&amp;lt;span style=&amp;quot;font-family: Arial;&amp;quot;&amp;gt;&amp;lt;b&amp;gt;&amp;lt;span lang=&amp;quot;EN-US&amp;quot;&amp;gt;WRITING TASK 1&amp;lt;/span&amp;gt;&amp;lt;/b&amp;gt;&amp;lt;/span&amp;gt;&amp;lt;/span&amp;gt;&amp;lt;span lang=&amp;quot;EN-US&amp;quot; style=&amp;quot;font-size: 12pt; font-family: Simsun, serif;&amp;quot;&amp;gt;&amp;lt;o:p&amp;gt;&amp;lt;/o:p&amp;gt;&amp;lt;/span&amp;gt;&amp;lt;/p&amp;gt;\n" +
                "&amp;lt;p class=&amp;quot;MsoNormal&amp;quot; style=&amp;quot;font-size: 12px; font-family: Simsun; line-height: 13.5pt;&amp;quot;&amp;gt;&amp;lt;span style=&amp;quot;font-size: larger;&amp;quot;&amp;gt;&amp;lt;span style=&amp;quot;font-family: Arial;&amp;quot;&amp;gt;&amp;lt;span lang=&amp;quot;EN-US&amp;quot;&amp;gt;You should spend about 20 minutes on this task.&amp;lt;/span&amp;gt;&amp;lt;/span&amp;gt;&amp;lt;/span&amp;gt;&amp;lt;span lang=&amp;quot;EN-US&amp;quot; style=&amp;quot;font-size: 12pt; font-family: Simsun, serif;&amp;quot;&amp;gt;&amp;lt;o:p&amp;gt;&amp;lt;/o:p&amp;gt;&amp;lt;/span&amp;gt;&amp;lt;/p&amp;gt;\n" +
                "&amp;lt;p class=&amp;quot;MsoNormal&amp;quot;&amp;gt;&amp;lt;span style=&amp;quot;font-size: larger;&amp;quot;&amp;gt;&amp;lt;span style=&amp;quot;font-family: Arial;&amp;quot;&amp;gt;&amp;lt;b&amp;gt;&amp;lt;span lang=&amp;quot;EN-US&amp;quot;&amp;gt;The table below gives information about the underground railway systems in six cities.&amp;lt;/span&amp;gt;&amp;lt;/b&amp;gt;&amp;lt;/span&amp;gt;&amp;lt;b&amp;gt;&amp;lt;span lang=&amp;quot;EN-US&amp;quot; /&amp;gt;&amp;lt;/b&amp;gt;&amp;lt;/span&amp;gt;&amp;lt;b&amp;gt;&amp;lt;span lang=&amp;quot;EN-US&amp;quot;&amp;gt;&amp;lt;font size=&amp;quot;4&amp;quot;&amp;gt;&amp;lt;o:p&amp;gt;&amp;lt;/o:p&amp;gt;&amp;lt;/font&amp;gt;&amp;lt;/span&amp;gt;&amp;lt;/b&amp;gt;&amp;lt;/p&amp;gt;\n" +
                "&amp;lt;p class=&amp;quot;MsoNormal&amp;quot;&amp;gt;&amp;lt;span style=&amp;quot;font-size: larger;&amp;quot;&amp;gt; &amp;lt;/span&amp;gt;&amp;lt;span style=&amp;quot;font-size: larger;&amp;quot;&amp;gt;&amp;lt;span style=&amp;quot;font-family: Arial;&amp;quot;&amp;gt;&amp;lt;span lang=&amp;quot;EN-US&amp;quot;&amp;gt;Write at least 150 words.&amp;lt;/span&amp;gt;&amp;lt;/span&amp;gt;&amp;lt;/span&amp;gt;&amp;lt;span style=&amp;quot;font-family: Arial;&amp;quot; /&amp;gt;&amp;lt;/p&amp;gt;\n" +
                "&amp;lt;span style=&amp;quot;font-family: Arial;&amp;quot;&amp;gt;&amp;lt;img src=&amp;quot;http://images.koolearn.com/uploadimage/images/admin/postil/proposition/picture/1394090728204.jpg&amp;quot; width=&amp;quot;500&amp;quot; height=&amp;quot;350&amp;quot; alt=&amp;quot;&amp;quot; /&amp;gt;&amp;lt;/span&amp;gt;&amp;lt;br/&amp;gt;\n" +
                "&amp;amp;nbsp;&amp;lt;br/&amp;gt;{{answer}}&amp;amp;nbsp;{{/answer}}\n" +
                "            <extendedTextInteraction responseIdentifier=\"\"RESPONSE\"\" expectedLength=\"\"5000\"\"/>\n" +
                "        </div>\n" +
                "            <rubricBlock view=\"\"scorer\"\" use=\"\"analysis\"\" label=\"\"SUBITEM_0\"\">\n" +
                "                <div label=\"\"wrap\"\">{{grading}}&amp;amp;nbsp;{{/grading}}</div>\n" +
                "            </rubricBlock>\n" +
                "    </itemBody>\n" +
                "    <responseProcessing>\n" +
                "    </responseProcessing>\n" +
                "</assessmentItem>\"";

        s = StringEscapeUtils.unescapeHtml3(oldDataHandler(s));
        s = StringEscapeUtils.unescapeHtml3(oldDataHandler(s));
        System.out.println("+++++++++++++" + s);
        String clean = Jsoup.clean(s, Whitelist.none());
        System.out.println("=======" + clean);
    }

    private String oldDataHandler(String xml) {
        String str = null;
        str = xml.replaceAll("\\b", "")
                .replaceAll("\b", "")
                .replaceAll("&lt;/u&gt;", "")
                .replaceAll("&lt;u&gt;", "");
        return str;
    }


    @Test
    public void beijing() throws Exception {
        String[] bjCourts=new String[]{"龙华园","龙腾苑","新龙城"};
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
    public void testLianjia4(){
    }
}
