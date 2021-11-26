package com.kevinlee.shardingjdbc.service;

import com.kevinlee.shardingjdbc.entity.User;
import com.kevinlee.shardingjdbc.entity.UserOrder;
import com.kevinlee.shardingjdbc.mapper.UserMapper;
import com.kevinlee.shardingjdbc.mapper.UserOrderMapper;
import io.shardingsphere.transaction.annotation.ShardingTransactionType;
import io.shardingsphere.transaction.api.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: 学相伴-飞哥
 * @description: UserService
 * @Date : 2021/3/14
 */
@Service
public class UserOrderService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserOrderMapper userOrderMapper;


    @Transactional(rollbackFor = Exception.class)
    @ShardingTransactionType(TransactionType.XA)//跨库使用
    //@ShardingTransactionType(TransactionType.LOCAL)// 同一库中可以使用
    public int saveUserOrder(User user, UserOrder userOrder) { // 没有加事务
        userMapper.addUser(user);// 立即写入数据库
        userOrder.setUserid(user.getId());
        int a = 1/0; //测试回滚，统一提交的话，将这行注释掉就行
        userOrderMapper.addUserOrder(userOrder);
        return 1;
    }
}