package com.shiyiju.modules.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AdminArtistSaveDTO {

    @NotBlank
    private String name;

    private String city;
    private String tags;
    private String avatarUrl;

    @NotNull
    @Min(0)
    private Integer works;

    private Integer sort;

    @NotBlank
    private String status;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public Integer getWorks() { return works; }
    public void setWorks(Integer works) { this.works = works; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
