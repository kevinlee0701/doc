package com.kevinlee.shardingjdbc.mapper;
import com.kevinlee.shardingjdbc.entity.Order;
import com.kevinlee.shardingjdbc.entity.UserOrder;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: 学相伴-飞哥
 * @description: UserMapper
 * @Date : 2021/3/10
 */
@Mapper
@Repository
public interface UserOrderMapper {
    /**
     * @author 学相伴-飞哥
     * @description 保存订单
     * @params [user]
     * @date 2021/3/10 17:14
     */
    @Insert("insert into ksd_user_order(ordernumber,userid,create_time,yearmonth) values(#{ordernumber},#{userid},#{createTime},#{yearmonth})")
    @Options(useGeneratedKeys = true,keyColumn = "orderid",keyProperty = "orderid")
    void addUserOrder(UserOrder userOrder);


    @Select("select * from ksd_user_order limit #{pageNo},10")
    List<UserOrder> finduserOrders(@Param("pageNo")Integer pageNo);


    @Select("select count(1) from ksd_user_order")
    int countuserOrders();
}