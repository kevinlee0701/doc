package com.kevinlee.shardingjdbc.controller;
import com.kevinlee.shardingjdbc.entity.User;
import com.kevinlee.shardingjdbc.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Random;
/**
 * @author: 学相伴-飞哥
 * @description: UserController
 * @Date : 2021/3/10
 */
@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserMapper userMapper;


    // 写
    @GetMapping("/save")
    public String insert() {
        User user = new User();
        user.setNickname("zhangsan"+ new Random().nextInt());
        user.setPassword("1234567");
        user.setSex(1);
        user.setBirthday(new Date());
        userMapper.addUser(user);
        return "success";
    }


    // 读 -- Random
    @GetMapping("/listuser")
    public List<User> listuser() {
        return userMapper.findUsers();
    }
}