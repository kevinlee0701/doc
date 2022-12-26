package com.kevinlee.freemarker;

/**
 * 类 描 述：测试freemarker
 * 创建时间：2022/11/25 09:44
 * 创 建 人：lifeng
 */
public class User {
    private Long id;
    private String username;
    private String address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
