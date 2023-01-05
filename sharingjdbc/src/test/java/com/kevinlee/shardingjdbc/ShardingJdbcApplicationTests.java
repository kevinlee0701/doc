package com.kevinlee.shardingjdbc;


import com.kevinlee.shardingjdbc.entity.User;
import com.kevinlee.shardingjdbc.entity.UserOrder;
import com.kevinlee.shardingjdbc.mapper.UserMapper;
import com.kevinlee.shardingjdbc.mapper.UserOrderMapper;
import com.kevinlee.shardingjdbc.service.UserOrderService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Random;

@SpringBootTest
@RunWith(SpringRunner.class)
class ShardingJdbcApplicationTests {


    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserOrderMapper userOrderMapper;
    @Autowired
    private UserOrderService userOrderService;

    @Test
    void contextLoads() {
        User user = new User();
        user.setNickname("xxxzhangsan"+ new Random().nextInt());
        user.setPassword("12345678");
        user.setSex(1);
        user.setAge(2);
        user.setBirthday(new Date());
        userMapper.addUser(user);
        System.out.println(user.getId());
    }

    @Test
    void selectUser() {
        System.out.println( userMapper.findUserById(671043090357682177l));
    }

    @Test
    public void orderyearMaster() {
        for (int i = 0; i <1 ; i++) {
            UserOrder userOrder = new UserOrder();
            userOrder.setCreateTime(new Date());
            userOrder.setOrdernumber("133455678");
            userOrder.setYear("2021");
            userOrder.setUserid(1L);
            userOrderMapper.addUserOrder(userOrder);
        }
    }
    @Test
    void selectUserOrder() {
//        List<UserOrder> year = userOrderMapper.findYear("2019");
//        System.out.println(year);

        List<UserOrder> userOrders = userOrderMapper.findorderId("672042168889638913");
        System.out.println(userOrders);

    }

    @Test
    public void countOrderyearMaster() {
        System.out.println(userOrderMapper.countuserOrders());
    }

    @Test
    public void selectOrderyearMaster() {
        // 性能 -- es
        System.out.println(userOrderMapper.finduserOrders(0));
        System.out.println("==================1======================");
        System.out.println(userOrderMapper.finduserOrders(10));
        System.out.println("==================2======================");
        System.out.println(userOrderMapper.finduserOrders(20));
        System.out.println("==================3======================");
        System.out.println(userOrderMapper.finduserOrders(30));
        System.out.println("==================4======================");
        System.out.println(userOrderMapper.finduserOrders(40));
        System.out.println("==================5======================");
        System.out.println(userOrderMapper.finduserOrders(50));
    }

    @Test
    public void testTranscation() {

        User user = new User();
        user.setNickname("xxxzhangsan"+ new Random().nextInt());
        user.setPassword("12345678");
        user.setSex(1);
        user.setAge(3);
        user.setBirthday(new Date());


        UserOrder userOrder = new UserOrder();
        userOrder.setCreateTime(new Date());
        userOrder.setOrdernumber("133455678");
        userOrder.setYear("202102");
        userOrder.setUserid(1L);

        userOrderService.saveUserOrder(user,userOrder);
    }

}
