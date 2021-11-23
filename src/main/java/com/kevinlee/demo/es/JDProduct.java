package com.kevinlee.demo.es;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @ClassName JDProduct
 * @Author kevinlee
 * @Date 2021/11/22 16:55
 * @Version 1.0
 **/
@Data
@ToString
@Document(indexName = "jd-product")
public class JDProduct {
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String img;
    @Field(type = FieldType.Keyword)
    private Double price;
    @Field(type = FieldType.Text)
    private String title;

}
