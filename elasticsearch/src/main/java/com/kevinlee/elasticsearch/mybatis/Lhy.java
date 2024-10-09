package com.kevinlee.elasticsearch.mybatis;

import lombok.Data;

import java.util.Date;

/**
 * 类 描 述：小区总价，均价
 * 创建时间：2024/9/30 下午6:16
 * 创 建 人：lifeng
 */
@Data
public class Lhy {
    private Double price;
    private String court;
    private Date createTime;
    private Double totalPrice;
    private Double southPrice;
    private Double southTotalPrice;
    private String remark;
}
