package com.shiyiju.modules.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdateCurrentUserDTO {

    @NotBlank
    private String nickname;
    private String avatarUrl;
    @Pattern(regexp = "^$|^1\\d{10}$")
    private String mobile;
    private Integer gender;

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
}
