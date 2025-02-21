package com.kevinlele.elasticsearch;

import java.util.*;

public class MultiLevelDataStructure {
    public static void main(String[] args) {

        List<String> level01 = new ArrayList<>();
        Map<String,List<String> >level02 = new HashMap();
        // 输入数据
        String[] data = {
            "直通车服务类型_标准直通车_公共课_英语-3",
            "直通车服务类型_其他-1"

        };
        for (String datum : data) {
            String[] s = datum.split("_");
            level01.contains(s[0]);
            if(s.length>=1){}
        }
    }
}
