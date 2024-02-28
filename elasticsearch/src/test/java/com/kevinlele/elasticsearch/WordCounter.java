package com.kevinlele.elasticsearch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordCounter {

    // 正则表达式，用于匹配单词
  private static  Pattern WORD_PATTERN = Pattern.compile("\\w+");
    /**
     * 计算给定文本中的单词数量
     *
     * @param text 要计算单词数量的文本
     * @return 单词数量
     */
    public static int countWords(String text) {
        Matcher matcher = WORD_PATTERN.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    // 测试方法
    public static void main(String[] args) {
        String text = "This is a 测试  test  How            many words are there?.";
        int wordCount = countWords(text);
        System.out.println("Number of words: " + wordCount);
    }
}
