package com.kevinlee.elasticsearch.pachong;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @ClassName LianJIa
 * @Author kevinlee
 * @Date 2021/11/23 15:11
 * @Version 1.0
 **/
@Data
@ToString
@Document(indexName = "lianjia_new1")
public class LianJia {
    @Id
    private String id;
    //房子地址
    @Field(type = FieldType.Auto)
    private String address;
    //房子网址
    @Field(type = FieldType.Auto)
    private String html;
    //总价
    @Field(type = FieldType.Auto)
    private Double totalPrice;
    //房屋面积
    @Field(type = FieldType.Auto)
    private Double area;
    //均价
    @Field(type = FieldType.Auto)
    private Double unitPrice;
    //房屋基础信息
    @Field(type = FieldType.Auto)
    private String houseInfo;
    //房子图片地址
    @Field(type = FieldType.Auto)
    private String img;
    //备注
    @Field(type = FieldType.Auto)
    private String remark;
    //创建时间
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;



}
