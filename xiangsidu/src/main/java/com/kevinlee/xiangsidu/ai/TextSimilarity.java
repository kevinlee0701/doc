package com.kevinlee.xiangsidu.ai;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TextSimilarity {

    public static void main(String[] args) {
        // 两个示例文本
        String text1 = "The chart indicates that how many people had five and more of sickness absence when they work on the 5 European countries between 1991 to 2001.\n" +
                "\n" +
                "It is clear that the proportion of worker in Netherlands remained the highest level over a period of 10 years between 5 countries.However, the percentage of Germany worker is the lowest in these countries from 1991 to 2001.In addition, the number of workers in other countries such as Smeden, France and the U.K. stayed at the middle number between Netherlands and Germany.\n" +
                "\n" +
                "There are decreases in the proportion of worker in Smeden , France and Germany(about 10%, 15% and 5%) from 1991 to 1992.In contrast, the number of worker of Netherland and Britain showed increase(about 10% and 5%)at the same time.\n" +
                "\n" +
                "Between 1992 and 1995, the percentage of worker in Netherlands had a greatly drop from 90% to 70%.While the amount of Britain and France workers had a little rise from 45% to 50% and from 50% to 55% in 3 years. The number of workers in other countries-Smeden and Germany had slight fluctuatiom(about 60% and30%) in this period.\n" +
                "\n" +
                "From 1995 to 2001,the amount of workers in Netherlands and Smeden hit a peak of themself at 90% and 75%. the number of employees in France , Britain and Germany remained the same in 6 years.";
        String text2 = "The&nbsp;<span class=\"useful-span\" data-left=\"178.5\" id=\"essay-error-path-1701589899749\">chart</span>&nbsp;indicates&nbsp;<span class=\"useful-span\" data-left=\"358.5\" id=\"essay-error-path-1701589936156\">that how many</span>&nbsp;people&nbsp;<span class=\"useful-span\" data-left=\"438.5\" id=\"essay-error-path-1701589970196\">had</span>&nbsp;<span class=\"useful-span\" data-left=\"446.5\" id=\"essay-error-path-1701590006512\">five</span>&nbsp;and more of sickness absence&nbsp;<span class=\"useful-span\" data-left=\"831.5\" id=\"essay-error-path-1701590039714\">when they&nbsp;work&nbsp;on the</span>&nbsp;5 European countries between 1991&nbsp;<span class=\"useful-span\" data-left=\"128.5\" id=\"essay-error-path-1701590053247\">to</span>&nbsp;2001.<br>It is clear that the proportion of&nbsp;<span class=\"useful-span\" data-left=\"345.5\" id=\"essay-error-path-1701590130805\">worker</span>&nbsp;in&nbsp;<span class=\"useful-span\" data-left=\"447.5\" id=\"essay-error-path-1701590183756\">Netherlands</span>&nbsp;remained&nbsp;<span class=\"useful-span\" data-left=\"563.5\" id=\"essay-error-path-1701590219179\">the</span>&nbsp;highest level over&nbsp;<span class=\"useful-span\" data-left=\"710.5\" id=\"essay-error-path-1701590233076\">a</span>&nbsp;period of 10 years&nbsp;<span class=\"useful-span\" data-left=\"860.5\" id=\"essay-error-path-1701590400166\">between</span>&nbsp;<span class=\"useful-span\" data-left=\"913.5\" id=\"essay-error-path-1701590383927\">5</span>&nbsp;countries.However, the percentage of Germany&nbsp;<span class=\"useful-span\" data-left=\"296.5\" id=\"essay-error-path-1701590437284\">worker</span>&nbsp;<span class=\"useful-span\" data-left=\"343.5\" id=\"essay-error-path-1701590458443\">is</span>&nbsp;the lowest&nbsp;<span class=\"useful-span\" data-left=\"677.5\" id=\"essay-error-path-1701590487393\">in these countries from 1991 to 2001</span>.In addition, the&nbsp;<span class=\"useful-span\" data-left=\"929.5\" id=\"essay-error-path-1701590526561\">number of workers in</span>&nbsp;other countries such as Smeden, France and the U.K. stayed at the middle&nbsp;<span class=\"useful-span\" data-left=\"485.5\" id=\"essay-error-path-1701590582403\">number</span>&nbsp;<span class=\"useful-span\" data-left=\"773.5\" id=\"essay-error-path-1701590591506\">between Netherlands and Germany</span>.<br>There&nbsp;<span class=\"useful-span\" data-left=\"256.5\" id=\"essay-error-path-1701590644789\">are decreases</span>&nbsp;in the proportion of&nbsp;<span class=\"useful-span\" data-left=\"412.5\" id=\"essay-error-path-1701590660471\">worker</span>&nbsp;in&nbsp;<span class=\"useful-span\" data-left=\"474.5\" id=\"essay-error-path-1701590759437\">Smeden</span>&nbsp;, France and Germany<span class=\"useful-span\" data-left=\"844.5\" id=\"essay-error-path-1701590772469\">(about 10%, 15% and 5%)</span>&nbsp;from 1991 to 1992.In contrast, the&nbsp;<span class=\"useful-span\" data-left=\"343.5\" id=\"essay-error-path-1701590829644\">number of worker of Netherland</span>&nbsp;and Britain showed&nbsp;<span class=\"useful-span\" data-left=\"561.5\" id=\"essay-error-path-1701590872893\">increase(</span>about 10% and 5%<span class=\"useful-span\" data-left=\"694.5\" id=\"essay-error-path-1701590891688\">)</span>at the same time.<br>Between 1992 and 1995, the percentage of&nbsp;<span class=\"useful-span\" data-left=\"492.5\" id=\"essay-error-path-1701591013938\">worker in</span>&nbsp;Netherlands had a&nbsp;<span class=\"useful-span\" data-left=\"642.5\" id=\"essay-error-path-1701591033705\">greatly</span>&nbsp;drop from 90% to 70%<span class=\"useful-span\" data-left=\"886.5\" id=\"essay-error-path-1701591056543\">.While</span>&nbsp;the&nbsp;<span class=\"useful-span\" data-left=\"921.5\" id=\"essay-error-path-1701591126828\">amount</span>&nbsp;of&nbsp;<span class=\"useful-span\" data-left=\"1004.5\" id=\"essay-error-path-1701591151299\" data-showatbegin=\"true\">Britain and France</span>&nbsp;workers had a little rise from&nbsp;<span class=\"useful-span\" data-left=\"637.5\" id=\"essay-error-path-1701591196059\">45% to 50% and from 50% to 55% in</span>&nbsp;3 years. The&nbsp;<span class=\"useful-span\" data-left=\"992.5\" id=\"essay-error-path-1701591250592\">number of workers in other countries-</span><span class=\"useful-span\" data-left=\"1013.5\" id=\"essay-error-path-1701591282199\">Smeden</span>&nbsp;and Germany had slight&nbsp;<span class=\"useful-span\" data-left=\"568.5\" id=\"essay-error-path-1701591303045\">fluctuatiom(about 60% and30%) in this period</span>.<br>From 1995 to 2001,the&nbsp;<span class=\"useful-span\" data-left=\"283.5\" id=\"essay-error-path-1701591460498\">amount</span>&nbsp;of workers&nbsp;<span class=\"useful-span\" data-left=\"423.5\" id=\"essay-error-path-1701591471015\">in</span>&nbsp;Netherlands and&nbsp;<span class=\"useful-span\" data-left=\"560.5\" id=\"essay-error-path-1701591482858\">Smeden</span>&nbsp;<span class=\"useful-span\" data-left=\"763.5\" id=\"essay-error-path-1701591493213\">hit a peak of themself&nbsp;</span>at 90% and 75%.&nbsp;<span class=\"useful-span\" data-left=\"1123.5\" id=\"essay-error-path-1701591535594\" data-showatbegin=\"true\">the number&nbsp;of employees in</span>&nbsp;France , Britain and Germany remained the same in&nbsp;<span class=\"useful-span\" data-left=\"486.5\" id=\"essay-error-path-1701591555522\">6</span>&nbsp;years.<br>\n";

        // 计算文本相似度
        double similarity = calculateTextSimilarity(text1, text2);

        // 输出结果
        System.out.println("Text Similarity: " + similarity);
    }

    // 计算文本相似度的方法
    public static double calculateTextSimilarity(String text1, String text2) {
        // 将文本分词并构建词袋模型
        Map<String, Integer> bagOfWords1 = buildBagOfWords(text1);
        Map<String, Integer> bagOfWords2 = buildBagOfWords(text2);

        // 计算余弦相似度
        double cosineSimilarity = calculateCosineSimilarity(bagOfWords1, bagOfWords2);

        return cosineSimilarity;
    }

    // 构建词袋模型的方法
    public static Map<String, Integer> buildBagOfWords(String text) {
        String[] words = text.toLowerCase().split("\\s+"); // 将文本转换为小写并分词
        Map<String, Integer> bagOfWords = new HashMap<>();

        // 统计词频
        for (String word : words) {
            bagOfWords.put(word, bagOfWords.getOrDefault(word, 0) + 1);
        }

        return bagOfWords;
    }

    // 计算余弦相似度的方法
    public static double calculateCosineSimilarity(Map<String, Integer> bagOfWords1, Map<String, Integer> bagOfWords2) {
        // 获取所有不重复的词
        String[] allWords = getAllWords(bagOfWords1, bagOfWords2);

        // 构建向量表示
        int[] vector1 = buildVector(allWords, bagOfWords1);
        int[] vector2 = buildVector(allWords, bagOfWords2);

        // 计算余弦相似度
        double cosineSimilarity = calculateCosineSimilarity(vector1, vector2);

        return cosineSimilarity;
    }

    // 获取所有不重复的词的方法
    public static String[] getAllWords(Map<String, Integer>... bagsOfWords) {
        return Arrays.stream(bagsOfWords)
                .flatMap(bag -> bag.keySet().stream())
                .distinct()
                .toArray(String[]::new);
    }

    // 构建向量表示的方法
    public static int[] buildVector(String[] allWords, Map<String, Integer> bagOfWords) {
        int[] vector = new int[allWords.length];

        for (int i = 0; i < allWords.length; i++) {
            vector[i] = bagOfWords.getOrDefault(allWords[i], 0);
        }

        return vector;
    }

    // 计算余弦相似度的方法
    public static double calculateCosineSimilarity(int[] vector1, int[] vector2) {
        // 计算点积
        int dotProduct = 0;
        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
        }

        // 计算向量长度
        double magnitude1 = calculateMagnitude(vector1);
        double magnitude2 = calculateMagnitude(vector2);

        // 计算余弦相似度
        double cosineSimilarity;
        if (magnitude1 == 0 || magnitude2 == 0) {
            cosineSimilarity = 0; // 避免除零错误
        } else {
            cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
        }

        return cosineSimilarity;
    }

    // 计算向量长度的方法
    public static double calculateMagnitude(int[] vector) {
        int sum = 0;
        for (int value : vector) {
            sum += value * value;
        }
        return Math.sqrt(sum);
    }
}
