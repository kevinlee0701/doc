package com.kevinlee.apollo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName SailfishProperties
 * @Author kevinlee
 * @Date 2022/3/30 14:08
 * @Version 1.0
 **/
@Component
public class SailfishProperties {

    @Value("${name}")

    private String appName;

    public String getAppName() {

        return appName;
    }
}
