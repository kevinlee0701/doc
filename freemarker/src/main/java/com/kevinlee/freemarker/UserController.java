package com.kevinlee.freemarker;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * 类 描 述：ceshi
 * 创建时间：2022/11/25 09:45
 * 创 建 人：lifeng
 */
@Controller
public class UserController {

    @GetMapping("/index")
    public String index(Model model) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            User user = new User();
            user.setId(1l);
            user.setUsername("用户名" + i);
            user.setAddress("<p><img src=\"https://daxue-cos.koocdn.com/uploadimg/2022/11/727495c77f6d326a4eca653ae6f2ce24.jpg\" /></p>");
            users.add(user);
        }
        model.addAttribute("users", users);
        return "index";
    }
}
