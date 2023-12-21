package com.kevin.juc;

import com.deepoove.poi.XWPFTemplate;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * 类 描 述：TODO
 * 创建时间：2023/12/20 16:50
 * 创 建 人：lifeng
 */
@Slf4j
public class VipMail {
    /**
     * 产品线加发送邮件场景
     * @throws IOException
     */
    @Test
    public void addFaJiaRenForProductLine() throws IOException {
      int[] productLines ={1,2,3,4,5};
      int business_type=1;//发送场景：1:排课邮件提醒,2:新vip规划报告提醒,5:教师学员第一次上课,6:小班课排课提醒;
      int[] mailIds={7,8};//数据库对应的邮箱
      int mailType=1;//1，发送者；2，抄送人,3收件人
        List<String> sqlList = new ArrayList<>();
        for (int productLine : productLines) {
            for (int mailId : mailIds) {
                StringBuilder sql = new StringBuilder();
                sql.append("insert into  pr_product_line_mail (business_type, product_line, mail_id, mail_type, create_time, update_time, is_delete)  values(")
                        .append(business_type).append(",")
                        .append(productLine).append(",")
                        .append(mailId).append(",")
                        .append(mailType).append(",")
                        .append("now()").append(",")
                        .append("now()").append(",")
                        .append("0)").append(";");
                sqlList.add(sql.toString());
                System.out.println(sql.toString());
            }
        }
    }
}
