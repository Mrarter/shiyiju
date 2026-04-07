package com.shiyiju.modules.user.vo;

public class LoginVO {

    private String token;
    private CurrentUserVO user;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public CurrentUserVO getUser() { return user; }
    public void setUser(CurrentUserVO user) { this.user = user; }
}
