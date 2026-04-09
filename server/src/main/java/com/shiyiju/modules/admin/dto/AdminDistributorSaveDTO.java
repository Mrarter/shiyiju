package com.shiyiju.modules.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AdminDistributorSaveDTO {

    @NotNull
    private Long userId;

    @NotBlank
    private String displayName;

    private String bio;

    @NotBlank
    private String status;

    private Integer teamLevel;

    private Long parentDistributorId;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getTeamLevel() { return teamLevel; }
    public void setTeamLevel(Integer teamLevel) { this.teamLevel = teamLevel; }
    public Long getParentDistributorId() { return parentDistributorId; }
    public void setParentDistributorId(Long parentDistributorId) { this.parentDistributorId = parentDistributorId; }
}
