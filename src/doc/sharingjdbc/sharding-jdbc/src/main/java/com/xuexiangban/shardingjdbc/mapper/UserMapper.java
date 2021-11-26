package com.kevinlee.shardingjdbc.mapper;
import com.kevinlee.shardingjdbc.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * @author: 学相伴-飞哥
 * @description: UserMapper
 * @Date : 2021/3/10
 */
@Repository
public interface UserMapper {

    /**
     *
     * @author 学相伴-飞哥
     * @description 保存用户
     * @params [user]
     * @date 2021/3/10 17:14
     */
    @Insert("insert into ksd_user(nickname,password,sex,birthday,age) values(#{nickname},#{password},#{sex},#{birthday},#{age})")
    @Options(useGeneratedKeys = true,keyColumn = "id",keyProperty = "id")
    void addUser(User user);

    /**
     * @author 学相伴-飞哥
     * @description 保存用户
     * @params [user]
     * @date 2021/3/10 17:14
     */
    @Select("select * from ksd_user")
    List<User> findUsers();
}