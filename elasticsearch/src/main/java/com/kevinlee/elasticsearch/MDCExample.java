package com.kevinlee.elasticsearch;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

public class MDCExample {
    private static final Logger logger = Logger.getLogger(MDCExample.class);

    public static void main(String[] args) {
        // 设置上下文信息
        MDC.put("userId", "123456");
        MDC.put("requestId", "987654");

        // 记录日志
        logger.info("This is a log message with MDC context.");

//        // 清理上下文信息
//        MDC.remove("userId");
//        MDC.remove("requestId");
    }
}
