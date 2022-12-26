package com.kevinlee.xiangsidu;

/**
 * 类 描 述：计算两个字符串的相识度
 * 创建时间：2022/12/26 10:48
 * 创 建 人：lifeng
 */
public class Similarity {
    public static final  String content1="wo ai koolearn test,bu dui";

    public static final  String content2="wo ai koolearn test,bu dui,ni shi";


    public static void main(String[] args) {

        double  score=CosineSimilarity.getSimilarity(content1,content2);
        System.out.println("相似度："+score);
    }

}
