package com.shiyiju.modules.user.dto;

import jakarta.validation.constraints.NotBlank;

public class WxLoginRequestDTO {

    @NotBlank
    private String code;
    private String avatarUrl;
    private String nickname;
    private String inviteCode;
    private Integer gender;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
}
