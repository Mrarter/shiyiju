package com.shiyiju.modules.admin.vo;

public class AdminDistributorVO {

    private Long id;
    private Long userId;
    private String nickname;
    private String avatarUrl;
    private String mobile;
    private String displayName;
    private String bio;
    private Integer teamLevel;
    private String teamLevelName;
    private String status;
    private String statusName;
    private Long parentDistributorId;
    private String parentDistributorName;
    private Integer directCount;
    private Integer teamCount;
    private String totalCommission;
    private String availableCommission;
    private String invitationCode;
    private String createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public Integer getTeamLevel() { return teamLevel; }
    public void setTeamLevel(Integer teamLevel) { this.teamLevel = teamLevel; }
    public String getTeamLevelName() { return teamLevelName; }
    public void setTeamLevelName(String teamLevelName) { this.teamLevelName = teamLevelName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }
    public Long getParentDistributorId() { return parentDistributorId; }
    public void setParentDistributorId(Long parentDistributorId) { this.parentDistributorId = parentDistributorId; }
    public String getParentDistributorName() { return parentDistributorName; }
    public void setParentDistributorName(String parentDistributorName) { this.parentDistributorName = parentDistributorName; }
    public Integer getDirectCount() { return directCount; }
    public void setDirectCount(Integer directCount) { this.directCount = directCount; }
    public Integer getTeamCount() { return teamCount; }
    public void setTeamCount(Integer teamCount) { this.teamCount = teamCount; }
    public String getTotalCommission() { return totalCommission; }
    public void setTotalCommission(String totalCommission) { this.totalCommission = totalCommission; }
    public String getAvailableCommission() { return availableCommission; }
    public void setAvailableCommission(String availableCommission) { this.availableCommission = availableCommission; }
    public String getInvitationCode() { return invitationCode; }
    public void setInvitationCode(String invitationCode) { this.invitationCode = invitationCode; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
