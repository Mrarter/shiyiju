package com.shiyiju.modules.admin.vo;

public class AdminLoginVO {

    private String token;
    private AdminLoginUserVO user;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public AdminLoginUserVO getUser() {
        return user;
    }

    public void setUser(AdminLoginUserVO user) {
        this.user = user;
    }
}
