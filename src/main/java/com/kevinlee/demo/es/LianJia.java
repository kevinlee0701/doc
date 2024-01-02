package kevinlee.demo.es;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @ClassName LianJIa
 * @Author kevinlee
 * @Date 2021/11/23 15:11
 * @Version 1.0
 **/
@Data
@ToString
@Document(indexName = "lianjia")
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
    //房子图片地址
    @Field(type = FieldType.Auto)
    private String img;
    //备注
    @Field(type = FieldType.Auto)
    private String remark;
    //单价
    @Field(type = FieldType.Auto)
    private Double unitPrice;
}
