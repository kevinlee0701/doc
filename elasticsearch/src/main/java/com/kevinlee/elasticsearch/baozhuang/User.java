package com.kevinlee.elasticsearch.baozhuang;

/**
 * 类 描 述：user索引实体类
 * 创建时间：2023/7/3 15:43
 * 创 建 人：lifeng
 */
public class User {
    private String sex;
    private String name;
    private Integer age;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getAge() {
        return age;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSex() {
        return sex;
    }
}
