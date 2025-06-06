package com.kevinlee.xiangsidu;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 类 描 述：分词器
 * 创建时间：2022/12/26 10:40
 * 创 建 人：lifeng
 */
@Slf4j
public class Tokenizer {
    /**
     * 分词*/
    public static List<Word> segment(String sentence) {

        //1、 采用HanLP中文自然语言处理中标准分词进行分词
        List<Term> termList = HanLP.segment(sentence);

        //上面控制台打印信息就是这里输出的
        log.info(termList.toString());

        //2、重新封装到Word对象中（term.word代表分词后的词语，term.nature代表改词的词性）
        return termList.stream().map(term -> new Word(term.word, term.nature.toString())).collect(Collectors.toList());
    }
}
